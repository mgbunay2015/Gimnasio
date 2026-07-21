package com.gym.bookings.service.impl;

import com.gym.bookings.dto.ClassBookingRequest;
import com.gym.bookings.dto.ClassBookingResponse;
import com.gym.bookings.exception.BadRequestException;
import com.gym.bookings.exception.ForbiddenException;
import com.gym.bookings.exception.ResourceNotFoundException;
import com.gym.bookings.exception.UnauthorizedException;
import com.gym.bookings.mapper.ClassBookingMapper;
import com.gym.bookings.model.BookingStatus;
import com.gym.bookings.model.ClassBooking;
import com.gym.bookings.repository.ClassBookingRepository;
import com.gym.bookings.security.JwtUserExtractor;
import com.gym.bookings.service.ClassBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion de la logica de negocio de reservas.
 * Autorizacion a nivel de dato: memberUsername / trainerUsername desde el JWT.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ClassBookingServiceImpl implements ClassBookingService {

    // Acceso a persistencia de reservas
    private final ClassBookingRepository classBookingRepository;
    // Conversion entidad <-> DTO
    private final ClassBookingMapper classBookingMapper;
    // Extraccion segura de identidad desde el JWT
    private final JwtUserExtractor jwtUserExtractor;

    /**
     * MEMBER crea una reserva. El memberUsername se toma del preferred_username del JWT.
     */
    @Override
    public ClassBookingResponse create(ClassBookingRequest request, Jwt jwt) {
        // Exige token presente
        requireJwt(jwt);
        // Solo el rol MEMBER puede crear reservas
        if (!jwtUserExtractor.isMember(jwt)) {
            throw new ForbiddenException("Solo un MEMBER puede crear reservas de clase");
        }
        // Valida que el cuerpo no sea nulo
        if (request == null) {
            throw new BadRequestException("El cuerpo de la peticion es obligatorio");
        }
        // Username del socio autenticado (NUNCA del body)
        String memberUsername = jwtUserExtractor.getUsername(jwt);
        // Construye la entidad con el username del token
        ClassBooking booking = classBookingMapper.toEntity(request, memberUsername);
        // Persiste y mapea a DTO de respuesta
        return classBookingMapper.toResponse(classBookingRepository.save(booking));
    }

    /**
     * MEMBER consulta unicamente sus propias reservas (autorizacion a nivel de dato).
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClassBookingResponse> findMyBookings(Jwt jwt) {
        // Exige token presente
        requireJwt(jwt);
        // Solo MEMBER puede usar este endpoint
        if (!jwtUserExtractor.isMember(jwt)) {
            throw new ForbiddenException("Solo un MEMBER puede consultar sus reservas");
        }
        // Obtiene preferred_username del token
        String memberUsername = jwtUserExtractor.getUsername(jwt);
        // Filtra por el socio autenticado
        return mapList(classBookingRepository.findByMemberUsername(memberUsername));
    }

    /**
     * TRAINER consulta reservas de las clases que el dicta (autorizacion a nivel de dato).
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClassBookingResponse> findMyClasses(Jwt jwt) {
        // Exige token presente
        requireJwt(jwt);
        // Solo TRAINER puede usar este endpoint
        if (!jwtUserExtractor.isTrainer(jwt)) {
            throw new ForbiddenException("Solo un TRAINER puede consultar sus clases");
        }
        // Obtiene preferred_username del entrenador autenticado
        String trainerUsername = jwtUserExtractor.getUsername(jwt);
        // Filtra por el entrenador autenticado
        return mapList(classBookingRepository.findByTrainerUsername(trainerUsername));
    }

    /**
     * TRAINER marca asistencia: solo valido si el estado actual es RESERVED
     * y la reserva pertenece a una clase del entrenador autenticado.
     */
    @Override
    public ClassBookingResponse markAttended(Long id, Jwt jwt) {
        // Exige token presente
        requireJwt(jwt);
        // Solo TRAINER puede marcar asistencia
        if (!jwtUserExtractor.isTrainer(jwt)) {
            throw new ForbiddenException("Solo un TRAINER puede marcar asistencia");
        }
        // Valida el id
        requireId(id);
        // Carga la reserva o lanza 404
        ClassBooking booking = getBooking(id);
        // Username del entrenador autenticado
        String trainerUsername = jwtUserExtractor.getUsername(jwt);
        // Autorizacion a nivel de dato: solo sus propias clases
        if (!booking.getTrainerUsername().equals(trainerUsername)) {
            throw new ForbiddenException("Solo puede marcar asistencia en sus propias clases");
        }
        // Solo se puede pasar de RESERVED a ATTENDED
        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new BadRequestException(
                    "Solo se puede marcar asistencia si el estado es RESERVED. Estado actual: "
                            + booking.getStatus());
        }
        // Cambia el estado a ATTENDED
        booking.setStatus(BookingStatus.ATTENDED);
        // Persiste y retorna DTO
        return classBookingMapper.toResponse(classBookingRepository.save(booking));
    }

    /**
     * ADMIN lista todas las reservas sin restriccion de propietario.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClassBookingResponse> findAll(Jwt jwt) {
        // Exige token presente
        requireJwt(jwt);
        // Solo ADMIN tiene acceso total
        if (!jwtUserExtractor.isAdmin(jwt)) {
            throw new ForbiddenException("Solo un ADMIN puede listar todas las reservas");
        }
        // Retorna todas las reservas del sistema
        return mapList(classBookingRepository.findAll());
    }

    /**
     * ADMIN elimina cualquier reserva del sistema.
     */
    @Override
    public void delete(Long id, Jwt jwt) {
        // Exige token presente
        requireJwt(jwt);
        // Solo ADMIN puede eliminar
        if (!jwtUserExtractor.isAdmin(jwt)) {
            throw new ForbiddenException("Solo un ADMIN puede eliminar reservas");
        }
        // Valida el id
        requireId(id);
        // Verifica existencia
        ClassBooking booking = getBooking(id);
        // Elimina el registro
        classBookingRepository.delete(booking);
    }

    // Busca por id o lanza ResourceNotFoundException
    private ClassBooking getBooking(Long id) {
        return classBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + id));
    }

    // Exige JWT no nulo
    private void requireJwt(Jwt jwt) {
        if (jwt == null) {
            throw new UnauthorizedException("Se requiere un Bearer token valido");
        }
    }

    // Exige id no nulo
    private void requireId(Long id) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
    }

    // Mapea lista de entidades a lista de DTOs
    private List<ClassBookingResponse> mapList(List<ClassBooking> bookings) {
        return bookings.stream().map(classBookingMapper::toResponse).toList();
    }
}
