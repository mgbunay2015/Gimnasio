# Sistema de Gestión de Gimnasio — Actividad 6
## Autenticación y Autorización con JWT, OAuth2/OIDC y Keycloak

**Autor:** Manuel Buñay

Microservicio Spring Boot protegido por Keycloak (realm `gym-system`) para reserva de cupos en clases grupales.

---

## Endpoints

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| `POST` | `/api/bookings` | MEMBER | Crear reserva |
| `GET` | `/api/bookings/my-bookings` | MEMBER | Mis reservas |
| `GET` | `/api/bookings/my-classes` | TRAINER | Inscritos de mis clases |
| `PATCH` | `/api/bookings/{id}/attend` | TRAINER | Marcar asistencia |
| `GET` | `/api/bookings` | ADMIN | Listar todas las reservas |
| `DELETE` | `/api/bookings/{id}` | ADMIN | Eliminar reserva |
| `GET` | `/api/public/health` | Público | Health check |

---

## Video

Carpeta: [`video/`](video/)