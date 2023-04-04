package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class ReservationServiceTest {
    ReservationRepository reservationRepository;

    @AfterEach
    void clear() {

    }

    @Test
    void 시간이_중복되면_예약불가() {
        // given
        LocalDateTime firstStart = LocalDateTime.of(2023, 4, 3, 12, 30);
        LocalDateTime firstEnd = LocalDateTime.of(2023, 4, 3, 15, 30);
        LocalDateTime secondStart = LocalDateTime.of(2023, 4, 3, 10, 30);
        LocalDateTime secondEnd = LocalDateTime.of(2023, 4, 3, 14, 30);
        ReservationRequestDto firstReservation = new ReservationRequestDto();
        ReservationRequestDto secondReservation = new ReservationRequestDto();
        firstReservation.setStart(firstStart);
        firstReservation.setEnd(firstEnd);
        secondReservation.setStart(secondStart);
        secondReservation.setEnd(secondEnd);

        // when


        // then
    }
}