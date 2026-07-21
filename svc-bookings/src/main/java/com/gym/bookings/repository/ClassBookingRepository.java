package com.gym.bookings.repository;

import com.gym.bookings.model.ClassBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia de ClassBooking.
 */
@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    // Busca todas las reservas de un socio por su username
    List<ClassBooking> findByMemberUsername(String memberUsername);

    // Busca todas las reservas de clases dictadas por un entrenador
    List<ClassBooking> findByTrainerUsername(String trainerUsername);
}
