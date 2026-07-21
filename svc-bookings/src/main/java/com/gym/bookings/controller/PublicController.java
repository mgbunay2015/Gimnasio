package com.gym.bookings.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints publicos (sin autenticacion) para verificar que el servicio esta vivo.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    // Health check simple para Docker / Nginx
    @GetMapping("/health")
    public Map<String, String> health() {
        // Retorna estado UP con nombre del servicio
        return Map.of(
                "status", "UP",
                "service", "gym-bookings-service"
        );
    }
}
