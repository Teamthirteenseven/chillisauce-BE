package com.example.chillisauce.reservations.dto.response;

import com.example.chillisauce.reservations.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime end;
    List<String> userList;

    public ReservationResponseDto(Reservation reservation){
        this.start = reservation.getStartTime();
        this.end = reservation.getEndTime();
    }

    public ReservationResponseDto(Reservation reservation, List<String> userList) {
        this.start = reservation.getStartTime();
        this.end = reservation.getEndTime();
        this.userList = userList;
    }


}
