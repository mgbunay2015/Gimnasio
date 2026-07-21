package com.gym.bookings.model;

/**
 * Estados posibles de una reserva de clase grupal.
 * RESERVED  = cupo reservado por el socio
 * ATTENDED  = el entrenador marco asistencia
 * CANCELLED = reserva cancelada
 */
public enum BookingStatus {
    // Reserva creada y pendiente de asistencia
    RESERVED,
    // El entrenador confirmo que el socio asistio
    ATTENDED,
    // La reserva fue cancelada
    CANCELLED
}
