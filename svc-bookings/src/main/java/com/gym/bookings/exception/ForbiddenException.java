package com.gym.bookings.exception;

/**
 * Excepcion lanzada cuando el usuario autenticado no tiene permiso (HTTP 403).
 */
public class ForbiddenException extends RuntimeException {

    // Constructor que recibe el mensaje de error
    public ForbiddenException(String message) {
        // Delega el mensaje a RuntimeException
        super(message);
    }
}
