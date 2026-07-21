package com.gym.bookings.dto;

import com.gym.bookings.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida para exponer una reserva sin revelar la entidad JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBookingResponse {

    // Identificador de la reserva
    private Long id;

    // Username del socio que reservo
    private String memberUsername;

    // Username del entrenador asignado
    private String trainerUsername;

    // Nombre de la clase
    private String className;

    // Fecha y hora de la clase
    private LocalDateTime classDate;

    // Estado actual de la reserva
    private BookingStatus status;

    // Fecha de creacion del registro
    private LocalDateTime createdAt;
}
