package com.gym.bookings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuracion CORS para permitir pruebas desde Postman y navegadores.
 */
@Configuration
public class CorsConfig {

    // Define la fuente de configuracion CORS aplicada a todas las rutas
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // Crea una configuracion CORS abierta para desarrollo/laboratorio
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite cualquier origen (laboratorio academico)
        configuration.setAllowedOriginPatterns(List.of("*"));
        // Metodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Encabezados permitidos, incluido Authorization
        configuration.setAllowedHeaders(List.of("*"));
        // Expone Authorization en la respuesta si es necesario
        configuration.setExposedHeaders(List.of("Authorization"));
        // No se usan cookies de sesion
        configuration.setAllowCredentials(false);

        // Registra la configuracion para todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        // Retorna la fuente CORS
        return source;
    }
}
