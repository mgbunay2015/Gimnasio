package com.gym.bookings.controller;

import com.gym.bookings.dto.ClassBookingRequest;
import com.gym.bookings.dto.ClassBookingResponse;
import com.gym.bookings.service.ClassBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de reservas de clases.
 * Endpoints y roles segun la tabla de la Actividad 6.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class ClassBookingController {

    // Servicio de logica de negocio
    private final ClassBookingService classBookingService;

    /**
     * Escenario 1: MEMBER crea una reserva.
     * El memberUsername se toma del JWT (preferred_username), no del body.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEMBER')")
    public ClassBookingResponse create(
            @Valid @RequestBody ClassBookingRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // Delega la creacion al servicio con el JWT autenticado
        return classBookingService.create(request, jwt);
    }

    /**
     * MEMBER consulta unicamente sus propias reservas.
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('MEMBER')")
    public List<ClassBookingResponse> myBookings(@AuthenticationPrincipal Jwt jwt) {
        // Delega al servicio filtrando por el socio autenticado
        return classBookingService.findMyBookings(jwt);
    }

    /**
     * Escenario 3: TRAINER consulta inscritos de sus clases.
     */
    @GetMapping("/my-classes")
    @PreAuthorize("hasRole('TRAINER')")
    public List<ClassBookingResponse> myClasses(@AuthenticationPrincipal Jwt jwt) {
        // Delega al servicio filtrando por el entrenador autenticado
        return classBookingService.findMyClasses(jwt);
    }

    /**
     * Escenario 5: TRAINER marca asistencia (RESERVED -> ATTENDED).
     */
    @PatchMapping("/{id}/attend")
    @PreAuthorize("hasRole('TRAINER')")
    public ClassBookingResponse markAttended(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        // Delega el cambio de estado al servicio
        return classBookingService.markAttended(id, jwt);
    }

    /**
     * Escenarios 2 y 4: ADMIN lista todas las reservas.
     * MEMBER que intente acceder recibe 403 Forbidden.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClassBookingResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
        // Delega el listado completo al servicio
        return classBookingService.findAll(jwt);
    }

    /**
     * Escenario 6: ADMIN elimina cualquier reserva (204 No Content).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        // Delega la eliminacion al servicio
        classBookingService.delete(id, jwt);
    }
}
