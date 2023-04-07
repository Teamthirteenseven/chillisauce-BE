package com.example.chillisauce.reservations.dto;

import com.example.chillisauce.reservations.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {
    LocalDateTime start;
    LocalDateTime end;
}
