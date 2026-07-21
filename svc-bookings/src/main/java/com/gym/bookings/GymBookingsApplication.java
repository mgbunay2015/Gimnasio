package com.gym.bookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Punto de entrada del microservicio de reservas de clases del gimnasio.
 * Protegido por Keycloak (OAuth2/OIDC + JWT) como Resource Server.
 */
@SpringBootApplication(scanBasePackages = "com.gym.bookings")
@EntityScan(basePackages = "com.gym.bookings.model")
@EnableJpaRepositories(basePackages = "com.gym.bookings.repository")
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true)
public class GymBookingsApplication {

    // Metodo principal que arranca el contexto de Spring Boot
    public static void main(String[] args) {
        // Lanza la aplicacion con la clase de configuracion actual
        SpringApplication.run(GymBookingsApplication.class, args);
    }
}
