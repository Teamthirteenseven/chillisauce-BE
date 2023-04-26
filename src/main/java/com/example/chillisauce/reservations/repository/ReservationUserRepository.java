package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.reservations.entity.ReservationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationUserRepository extends JpaRepository<ReservationUser, Long> {
}
