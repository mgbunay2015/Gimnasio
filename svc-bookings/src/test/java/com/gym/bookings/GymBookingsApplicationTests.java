package com.gym.bookings;

import org.junit.jupiter.api.Test;

/**
 * Prueba de humo sin levantar el contexto completo
 * (el Resource Server requiere Keycloak en runtime).
 */
class GymBookingsApplicationTests {

    @Test
    void smoke() {
        // Verifica que las clases principales cargan en el classloader
        org.junit.jupiter.api.Assertions.assertNotNull(GymBookingsApplication.class);
    }
}
