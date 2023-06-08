package com.example.chillisauce.domain.reservations.dto;

import java.time.LocalDateTime;

public interface ReservationDetailWrapper {
    Long getReservationId();
    Long getMrId();
    String getMrName();
    String getUsername();
    LocalDateTime getStart();
    LocalDateTime getEnd();

}
