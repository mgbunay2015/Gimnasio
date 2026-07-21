package com.gym.bookings.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una reserva de cupo en una clase grupal.
 * El memberUsername SIEMPRE proviene del JWT, nunca del cuerpo de la peticion.
 */
@Entity
@Table(name = "class_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBooking {

    // Identificador unico autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username del socio obtenido del claim preferred_username del JWT
    @Column(nullable = false, length = 100)
    private String memberUsername;

    // Username del entrenador asignado a la clase
    @Column(nullable = false, length = 100)
    private String trainerUsername;

    // Nombre de la clase (Spinning, Yoga, CrossFit, etc.)
    @Column(nullable = false, length = 100)
    private String className;

    // Fecha y hora programada de la clase
    @Column(nullable = false)
    private LocalDateTime classDate;

    // Estado de la reserva: RESERVED, ATTENDED o CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    // Marca de tiempo de creacion del registro
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Antes de persistir, asigna estado inicial y fecha de creacion
    @PrePersist
    void onCreate() {
        // Guarda el momento actual como fecha de creacion
        createdAt = LocalDateTime.now();
        // Si no se indico estado, inicia como RESERVED
        if (status == null) {
            status = BookingStatus.RESERVED;
        }
    }
}
