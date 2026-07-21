package com.gym.bookings.exception;

/**
 * Excepcion lanzada ante datos invalidos en la peticion (HTTP 400).
 */
public class BadRequestException extends RuntimeException {

    // Constructor que recibe el mensaje de error
    public BadRequestException(String message) {
        // Delega el mensaje a RuntimeException
        super(message);
    }
}
