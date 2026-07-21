package com.gym.bookings.service;

import com.gym.bookings.dto.ClassBookingRequest;
import com.gym.bookings.dto.ClassBookingResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

/**
 * Contrato del servicio de reservas de clases.
 * Separacion de la logica de negocio respecto al controlador.
 */
public interface ClassBookingService {

    // MEMBER: crea una reserva a nombre del socio autenticado
    ClassBookingResponse create(ClassBookingRequest request, Jwt jwt);

    // MEMBER: lista solo las reservas del socio autenticado
    List<ClassBookingResponse> findMyBookings(Jwt jwt);

    // TRAINER: lista reservas de las clases que dicta el entrenador autenticado
    List<ClassBookingResponse> findMyClasses(Jwt jwt);

    // TRAINER: marca una reserva como ATTENDED (solo si estaba RESERVED)
    ClassBookingResponse markAttended(Long id, Jwt jwt);

    // ADMIN: lista todas las reservas del sistema
    List<ClassBookingResponse> findAll(Jwt jwt);

    // ADMIN: elimina cualquier reserva por id
    void delete(Long id, Jwt jwt);
}
