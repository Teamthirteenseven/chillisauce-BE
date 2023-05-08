package com.example.chillisauce.reservations.dto.response;

import com.example.chillisauce.reservations.dto.ReservationDetailWrapper;
import com.example.chillisauce.reservations.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
public class ReservationDetailResponse implements ReservationDetailWrapper {
    Long reservationId;
    Long mrId;
    String mrName;
    String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime end;
//    List<String> userList;

    public ReservationDetailResponse(Reservation reservation, Long mrId, String username) {
        this.reservationId = reservation.getId();
        this.mrId = mrId;
        this.mrName = reservation.getMeetingRoom().getLocationName();
        this.username = username;
        this.start = reservation.getStartTime();
        this.end = reservation.getEndTime();
    }

    public ReservationDetailResponse(Long reservationId, Long mrId, String mrName, String username,
                                     LocalDateTime start, LocalDateTime end) {
        this.reservationId = reservationId;
        this.mrId = mrId;
        this.mrName = mrName;
        this.username = username;
        this.start = start;
        this.end = end;
    }
}
