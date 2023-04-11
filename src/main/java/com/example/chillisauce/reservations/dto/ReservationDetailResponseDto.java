package com.example.chillisauce.reservations.dto;

import com.example.chillisauce.reservations.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
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
