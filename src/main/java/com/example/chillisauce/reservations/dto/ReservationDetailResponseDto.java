package com.example.chillisauce.reservations.dto;

import com.example.chillisauce.reservations.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDetailResponseDto {
    Long reservationId;
    Long mrId;
    String username;
    LocalDateTime start;
    LocalDateTime end;
//    List<String> userList;

    public ReservationDetailResponseDto(Reservation reservation) {
        this.reservationId= reservation.getId();
        this.mrId=reservation.getMeetingRoom().getId();
        this.username=reservation.getUser().getUsername();
        this.start=reservation.getStartTime();
        this.end=reservation.getEndTime();
    }
}
