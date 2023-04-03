package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.reservations.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
