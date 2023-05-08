package com.example.chillisauce.reservations.dto;

import java.time.LocalDateTime;

public interface ReservationDetailWrapper {
    Long getReservationId();
    Long getMrId();
    String getMrName();
    String getUsername();
    LocalDateTime getStart();
    LocalDateTime getEnd();

}
