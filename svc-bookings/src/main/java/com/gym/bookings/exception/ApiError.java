package com.gym.bookings.exception;

import java.time.LocalDateTime;

/**
 * Cuerpo estandar de error devuelto por la API.
 */
public record ApiError(
        // Momento en que ocurrio el error
        LocalDateTime timestamp,
        // Codigo HTTP numerico
        int status,
        // Frase del estado HTTP
        String error,
        // Mensaje descriptivo
        String message,
        // Ruta de la peticion que fallo
        String path
) {
}
