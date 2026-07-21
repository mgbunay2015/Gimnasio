package com.gym.bookings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configura el microservicio como OAuth2 Resource Server.
 * Extrae roles desde realm_access.roles y aplica autorizacion por ruta/rol.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Roles de realm definidos en Keycloak (mayusculas segun la actividad)
    public static final String ROLE_MEMBER = "MEMBER";
    public static final String ROLE_TRAINER = "TRAINER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Conjunto de roles de la aplicacion para filtrar roles internos de Keycloak
    private static final Set<String> APP_ROLES = Set.of(ROLE_MEMBER, ROLE_TRAINER, ROLE_ADMIN);

    /**
     * Cadena de filtros de seguridad: sin estado, CSRF desactivado, JWT obligatorio.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva CSRF porque la API es stateless con JWT
                .csrf(csrf -> csrf.disable())
                // Habilita CORS con la configuracion por defecto del bean CorsConfigurationSource
                .cors(Customizer.withDefaults())
                // Sesion STATELESS: cada peticion se autentica con el token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Reglas de autorizacion por ruta y rol (tabla de la actividad)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publicos de salud
                        .requestMatchers("/api/public/**").permitAll()
                        // Preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // POST /api/bookings -> solo MEMBER crea reservas
                        .requestMatchers(HttpMethod.POST, "/api/bookings")
                        .hasRole(ROLE_MEMBER)

                        // GET /api/bookings/my-bookings -> solo MEMBER ve sus reservas
                        .requestMatchers(HttpMethod.GET, "/api/bookings/my-bookings")
                        .hasRole(ROLE_MEMBER)

                        // GET /api/bookings/my-classes -> solo TRAINER ve inscritos de sus clases
                        .requestMatchers(HttpMethod.GET, "/api/bookings/my-classes")
                        .hasRole(ROLE_TRAINER)

                        // PATCH /api/bookings/{id}/attend -> solo TRAINER marca asistencia
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/attend")
                        .hasRole(ROLE_TRAINER)

                        // GET /api/bookings -> solo ADMIN lista todas las reservas
                        .requestMatchers(HttpMethod.GET, "/api/bookings")
                        .hasRole(ROLE_ADMIN)

                        // DELETE /api/bookings/{id} -> solo ADMIN elimina
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**")
                        .hasRole(ROLE_ADMIN)

                        // Cualquier otra ruta requiere autenticacion
                        .anyRequest().authenticated())
                // Configura el Resource Server para validar JWT de Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        // Construye y retorna la cadena de filtros
        return http.build();
    }

    /**
     * Convierte el JWT de Keycloak en un Authentication con autoridades ROLE_*.
     */
    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        // Crea el convertidor estandar de autenticacion JWT
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // Asigna el extractor personalizado de roles desde realm_access
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        // Retorna el bean listo para Spring Security
        return converter;
    }

    /**
     * Extrae scopes OIDC y roles de realm_access.roles del token Keycloak.
     * @param jwt token validado
     * @return coleccion de GrantedAuthority (ROLE_MEMBER, ROLE_TRAINER, ROLE_ADMIN)
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Lista acumuladora de autoridades
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Agrega los scopes estandar (SCOPE_*)
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> scopes = scopesConverter.convert(jwt);
        if (scopes != null) {
            authorities.addAll(scopes);
        }

        // Lee el claim realm_access del token de Keycloak
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        // Si existen roles de realm, los convierte a ROLE_XXX
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            List<GrantedAuthority> roleAuthorities = roles.stream()
                    // Convierte cada rol a String
                    .map(Object::toString)
                    // Conserva mayusculas (MEMBER, TRAINER, ADMIN)
                    .map(String::toUpperCase)
                    // Filtra solo los roles de la aplicacion
                    .filter(APP_ROLES::contains)
                    // Prefija ROLE_ para compatibilidad con hasRole()
                    .map(role -> "ROLE_" + role)
                    // Crea SimpleGrantedAuthority
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            // Agrega las autoridades de rol a la coleccion final
            authorities.addAll(roleAuthorities);
        }

        // Retorna todas las autoridades extraidas
        return authorities;
    }
}
