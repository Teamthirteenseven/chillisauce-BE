package com.example.chillisauce.reservations.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserReservationListResponse {
    List<UserReservationResponse> reservationList;
}
