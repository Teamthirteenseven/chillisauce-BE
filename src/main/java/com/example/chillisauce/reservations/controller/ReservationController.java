package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 회의실의 예약 1건 조회
     */
    @GetMapping("/reservations")
    public ResponseEntity getReservations(@RequestBody ReservationRequestDto requestDto) {
        return ResponseEntity.ok().body(reservationService.getReservation(requestDto));
    }
}
