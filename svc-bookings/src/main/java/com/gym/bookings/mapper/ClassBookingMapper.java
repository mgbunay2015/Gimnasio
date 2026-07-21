package com.gym.bookings.mapper;

import com.gym.bookings.dto.ClassBookingRequest;
import com.gym.bookings.dto.ClassBookingResponse;
import com.gym.bookings.model.BookingStatus;
import com.gym.bookings.model.ClassBooking;
import org.springframework.stereotype.Component;

/**
 * Convierte entre entidad ClassBooking y sus DTOs de entrada/salida.
 */
@Component
public class ClassBookingMapper {

    /**
     * Construye la entidad a partir del DTO de entrada y el username del JWT.
     * @param request datos de la clase a reservar
     * @param memberUsername username del socio autenticado (preferred_username)
     * @return entidad lista para persistir
     */
    public ClassBooking toEntity(ClassBookingRequest request, String memberUsername) {
        // Crea la entidad con builder de Lombok
        return ClassBooking.builder()
                // Asigna el socio desde el token, nunca desde el body
                .memberUsername(memberUsername)
                // Asigna el entrenador indicado en la peticion
                .trainerUsername(request.getTrainerUsername().trim())
                // Asigna el nombre de la clase
                .className(request.getClassName().trim())
                // Asigna la fecha/hora de la clase
                .classDate(request.getClassDate())
                // Estado inicial de toda reserva nueva
                .status(BookingStatus.RESERVED)
                .build();
    }

    /**
     * Convierte la entidad JPA a DTO de respuesta.
     * @param booking entidad persistida
     * @return DTO seguro para la API
     */
    public ClassBookingResponse toResponse(ClassBooking booking) {
        // Mapea campo por campo hacia el DTO de salida
        return ClassBookingResponse.builder()
                .id(booking.getId())
                .memberUsername(booking.getMemberUsername())
                .trainerUsername(booking.getTrainerUsername())
                .className(booking.getClassName())
                .classDate(booking.getClassDate())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
