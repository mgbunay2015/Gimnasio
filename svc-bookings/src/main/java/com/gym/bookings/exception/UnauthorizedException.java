package com.gym.bookings.exception;

/**
 * Excepcion lanzada cuando falta o es invalido el token Bearer (HTTP 401).
 */
public class UnauthorizedException extends RuntimeException {

    // Constructor que recibe el mensaje de error
    public UnauthorizedException(String message) {
        // Delega el mensaje a RuntimeException
        super(message);
    }
}
