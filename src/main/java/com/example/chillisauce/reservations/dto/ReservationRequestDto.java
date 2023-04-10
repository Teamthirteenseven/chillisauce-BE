package com.example.chillisauce.reservations.dto;

import com.example.chillisauce.reservations.entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예약 요청 DTO")
public class ReservationRequestDto {
    LocalDateTime start;
    LocalDateTime end;
}
