package com.gym.bookings.security;

import com.gym.bookings.exception.UnauthorizedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Extrae de forma segura la identidad y roles del JWT ya validado por Keycloak.
 * Nunca se confia en valores enviados por el cliente en el cuerpo de la peticion.
 */
@Component
public class JwtUserExtractor {

    /**
     * Obtiene el subject (sub) del token.
     * @param jwt token JWT autenticado
     * @return identificador unico del usuario en Keycloak
     */
    public String getUserId(Jwt jwt) {
        // Verifica que el token no sea nulo
        requireJwt(jwt);
        // Retorna el claim sub
        return jwt.getSubject();
    }

    /**
     * Obtiene el username desde preferred_username (requerido por la actividad).
     * @param jwt token JWT autenticado
     * @return preferred_username o, en su defecto, el subject
     */
    public String getUsername(Jwt jwt) {
        // Verifica que el token no sea nulo
        requireJwt(jwt);
        // Lee el claim preferred_username del token de Keycloak
        String username = jwt.getClaimAsString("preferred_username");
        // Si existe preferred_username lo usa; si no, cae al subject
        return username != null ? username : jwt.getSubject();
    }

    /**
     * Extrae los roles de realm desde realm_access.roles.
     * @param jwt token JWT autenticado
     * @return lista de nombres de rol
     */
    public List<String> getRoles(Jwt jwt) {
        // Verifica que el token no sea nulo
        requireJwt(jwt);
        // Obtiene el mapa realm_access del token
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        // Si no hay realm_access o roles, retorna lista vacia
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }
        // Obtiene el valor de roles
        Object roles = realmAccess.get("roles");
        // Si es una coleccion, la convierte a lista de String
        if (roles instanceof Collection<?> collection) {
            return collection.stream().map(String::valueOf).toList();
        }
        // En cualquier otro caso retorna lista vacia
        return List.of();
    }

    /**
     * Verifica si el token contiene un rol determinado (comparacion case-insensitive).
     * @param jwt token JWT
     * @param role nombre del rol a buscar
     * @return true si el usuario tiene ese rol
     */
    public boolean hasRole(Jwt jwt, String role) {
        // Recorre los roles y compara ignorando mayusculas/minusculas
        return getRoles(jwt).stream()
                .anyMatch(r -> r.equalsIgnoreCase(role) || r.equalsIgnoreCase("ROLE_" + role));
    }

    // Atajo: ¿tiene rol MEMBER?
    public boolean isMember(Jwt jwt) {
        return hasRole(jwt, "MEMBER");
    }

    // Atajo: ¿tiene rol TRAINER?
    public boolean isTrainer(Jwt jwt) {
        return hasRole(jwt, "TRAINER");
    }

    // Atajo: ¿tiene rol ADMIN?
    public boolean isAdmin(Jwt jwt) {
        return hasRole(jwt, "ADMIN");
    }

    // Lanza 401 si el JWT es nulo
    private void requireJwt(Jwt jwt) {
        if (jwt == null) {
            throw new UnauthorizedException("Se requiere un Bearer token valido");
        }
    }
}
