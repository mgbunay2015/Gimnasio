package com.gym.bookings.exception;

/**
 * Excepcion lanzada cuando un recurso solicitado no existe (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {

    // Constructor que recibe el mensaje de error
    public ResourceNotFoundException(String message) {
        // Delega el mensaje a RuntimeException
        super(message);
    }
}
