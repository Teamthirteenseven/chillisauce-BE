package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.reservations.dto.ReservationUserWrapper;
import com.example.chillisauce.reservations.entity.ReservationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationUserRepository extends JpaRepository<ReservationUser, Long> {
    @Query("select r.attendee.username as username, r.reservation.id as reservationId " +
            "from ReservationUser r " +
            "left join r.attendee " +
            "left join r.reservation where r.reservation.id in :reservationId")
    List<ReservationUserWrapper> findReservationUserByReservationIdIn(@Param("reservationId") List<Long> reservationId);
}
