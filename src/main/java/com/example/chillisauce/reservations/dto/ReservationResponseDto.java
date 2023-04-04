package com.example.chillisauce.reservations.dto;

import com.example.chillisauce.reservations.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponseDto {
    LocalDateTime start;
    LocalDateTime end;

    public ReservationResponseDto(Reservation reservation) {
        this.start = reservation.getStartTime();
        this.end = reservation.getEndTime();
    }
}
