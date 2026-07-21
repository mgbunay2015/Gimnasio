# Explicación de las peticiones Postman — Gym System

Colección: `Gym_System.postman_collection.json`

**Orden recomendado:** primero obtener los 3 tokens, luego ejecutar los escenarios 1 → 6.

Antes de empezar, confirma que Docker esté arriba con:
`GET http://localhost:8281/api/public/health` → debe responder `200` con `{"status":"UP",...}`.

---

## Carpeta: 00 - Obtener Tokens Keycloak

Estas peticiones piden un JWT a Keycloak (Password Grant). El script de Tests guarda el token en una variable de colección para usarlo después.

### 1. Token MEMBER (`member.test`)

| Campo | Valor |
|-------|-------|
| Método | `POST` |
| URL | `http://localhost:8280/realms/gym-system/protocol/openid-connect/token` |
| Usuario | `member.test` / `member123` |
| Rol | MEMBER |
| Esperado | **200** — guarda `memberToken` |

**Para qué sirve:** autenticar al socio. Con este token puede crear reservas y ver solo las suyas.

---

### 2. Token TRAINER (`trainer.test`)

| Campo | Valor |
|-------|-------|
| Método | `POST` |
| URL | misma URL de token Keycloak |
| Usuario | `trainer.test` / `trainer123` |
| Rol | TRAINER |
| Esperado | **200** — guarda `trainerToken` |

**Para qué sirve:** autenticar al entrenador. Puede ver inscritos de sus clases y marcar asistencia.

---

### 3. Token ADMIN (`admin.test`)

| Campo | Valor |
|-------|-------|
| Método | `POST` |
| URL | misma URL de token Keycloak |
| Usuario | `admin.test` / `admin123` |
| Rol | ADMIN |
| Esperado | **200** — guarda `adminToken` |

**Para qué sirve:** autenticar al administrador. Puede listar todas las reservas y eliminar cualquiera.

---

## Carpeta: 6 Escenarios de Prueba

### Escenario 1 — MEMBER crea reserva (201 Created)

| Campo | Valor |
|-------|-------|
| Método | `POST` |
| URL | `http://localhost:8281/api/bookings` |
| Auth | Bearer `{{memberToken}}` |
| Esperado | **201 Created** |

**Body:**
```json
{
  "trainerUsername": "trainer.test",
  "className": "Spinning",
  "classDate": "2026-08-15T10:00:00"
}
```

**Qué prueba:** un MEMBER puede crear una reserva. El `memberUsername` sale del JWT (no del body). El estado queda `RESERVED`. Guarda el `id` en `bookingId` para los escenarios 5 y 6.

---

### Escenario 2 — MEMBER intenta ver todas (403 Forbidden)

| Campo | Valor |
|-------|-------|
| Método | `GET` |
| URL | `http://localhost:8281/api/bookings` |
| Auth | Bearer `{{memberToken}}` |
| Esperado | **403 Forbidden** |

**Qué prueba:** el MEMBER **no** tiene permiso para listar todas las reservas. Solo ADMIN puede. Demuestra que la autorización por rol funciona.

---

### Escenario 3 — TRAINER consulta sus clases (200 OK)

| Campo | Valor |
|-------|-------|
| Método | `GET` |
| URL | `http://localhost:8281/api/bookings/my-classes` |
| Auth | Bearer `{{trainerToken}}` |
| Esperado | **200 OK** |

**Qué prueba:** el TRAINER ve solo las reservas de sus clases (`trainerUsername` = `trainer.test`). No ve clases de otros entrenadores.

---

### Escenario 4 — ADMIN lista todas (200 OK)

| Campo | Valor |
|-------|-------|
| Método | `GET` |
| URL | `http://localhost:8281/api/bookings` |
| Auth | Bearer `{{adminToken}}` |
| Esperado | **200 OK** (array) |

**Qué prueba:** el ADMIN sí puede listar **todas** las reservas del sistema (lo que el MEMBER no pudo en el escenario 2).

---

### Escenario 5 — TRAINER marca asistencia (200 OK)

| Campo | Valor |
|-------|-------|
| Método | `PATCH` |
| URL | `http://localhost:8281/api/bookings/{{bookingId}}/attend` |
| Auth | Bearer `{{trainerToken}}` |
| Esperado | **200 OK** — status `ATTENDED` |

**Qué prueba:** el TRAINER cambia el estado de la reserva de `RESERVED` a `ATTENDED`. Usa el `bookingId` guardado en el escenario 1.

---

### Escenario 6 — ADMIN elimina reserva (204 No Content)

| Campo | Valor |
|-------|-------|
| Método | `DELETE` |
| URL | `http://localhost:8281/api/bookings/{{bookingId}}` |
| Auth | Bearer `{{adminToken}}` |
| Esperado | **204 No Content** |

**Qué prueba:** el ADMIN puede eliminar cualquier reserva. Respuesta sin cuerpo.

---

## Carpeta: Extras

### MEMBER consulta mis reservas

| Campo | Valor |
|-------|-------|
| Método | `GET` |
| URL | `http://localhost:8281/api/bookings/my-bookings` |
| Auth | Bearer `{{memberToken}}` |
| Esperado | **200 OK** |

**Qué hace:** lista solo las reservas del socio autenticado (no todas).

---

### Health publico

| Campo | Valor |
|-------|-------|
| Método | `GET` |
| URL | `http://localhost:8281/api/public/health` |
| Auth | Ninguna |
| Esperado | **200** — `{"status":"UP","service":"gym-bookings-service"}` |

**Qué hace:** comprueba que el microservicio está vivo. No requiere token.

---

### Via Nginx Gateway - Health

| Campo | Valor |
|-------|-------|
| Método | `GET` |
| URL | `http://localhost:8088/api/public/health` |
| Auth | Ninguna |
| Esperado | **200** — mismo health |

**Qué hace:** misma comprobación, pero pasando por el gateway Nginx (puerto `8088` en lugar de `8281`).

---

## Resumen rápido

| # | Petición | Rol | Código esperado | Idea |
|---|----------|-----|-----------------|------|
| T1 | Token MEMBER | — | 200 | Obtener JWT socio |
| T2 | Token TRAINER | — | 200 | Obtener JWT entrenador |
| T3 | Token ADMIN | — | 200 | Obtener JWT admin |
| 1 | Crear reserva | MEMBER | 201 | Autorizado a crear |
| 2 | Listar todas | MEMBER | 403 | Denegado |
| 3 | Mis clases | TRAINER | 200 | Solo sus clases |
| 4 | Listar todas | ADMIN | 200 | Acceso total |
| 5 | Marcar asistencia | TRAINER | 200 | `RESERVED` → `ATTENDED` |
| 6 | Eliminar reserva | ADMIN | 204 | Borrado OK |
| E1 | Mis reservas | MEMBER | 200 | Solo las suyas |
| E2 | Health directo | Público | 200 | Servicio arriba |
| E3 | Health Nginx | Público | 200 | Gateway arriba |
