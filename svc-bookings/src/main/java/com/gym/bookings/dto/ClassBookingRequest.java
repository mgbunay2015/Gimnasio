package com.gym.bookings.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada para crear una reserva de clase.
 * NO incluye memberUsername: ese valor se toma del JWT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBookingRequest {

    // Username del entrenador que dicta la clase
    @NotBlank(message = "trainerUsername es obligatorio")
    private String trainerUsername;

    // Nombre de la clase grupal a reservar
    @NotBlank(message = "className es obligatorio")
    private String className;

    // Fecha y hora futura de la clase
    @NotNull(message = "classDate es obligatorio")
    @Future(message = "classDate debe ser una fecha futura")
    private LocalDateTime classDate;
}
