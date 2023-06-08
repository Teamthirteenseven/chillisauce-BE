package com.example.chillisauce.domain.reservations.dto.response;

import com.example.chillisauce.domain.reservations.dto.ReservationDetailWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
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
