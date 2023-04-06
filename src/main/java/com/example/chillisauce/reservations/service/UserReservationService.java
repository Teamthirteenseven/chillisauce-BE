package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.UserReservationListResponseDto;
import com.example.chillisauce.reservations.dto.UserReservationResponseDto;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReservationService {
    private final ReservationRepository reservationRepository;

    /**
     * 특정 유저의 예약 내역 조회
     */
    @Transactional(readOnly = true)
    public UserReservationListResponseDto getUserReservations(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        List<Reservation> userReservations = reservationRepository.findAllByUserId(user.getId());

        List<UserReservationResponseDto> responseDtos =
                userReservations.stream().map(UserReservationResponseDto::new).toList();

        return new UserReservationListResponseDto(responseDtos);
    }
}
