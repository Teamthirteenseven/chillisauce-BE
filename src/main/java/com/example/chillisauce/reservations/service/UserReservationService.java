package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.response.UserReservationListResponseDto;
import com.example.chillisauce.reservations.dto.response.UserReservationResponseDto;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Mr;
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

        return new UserReservationListResponseDto(userReservations.stream().map(x -> {
            Mr m = x.getMeetingRoom();
            User u = x.getUser();
            Long mrId = m == null ? 0 : m.getId();
            String username = u == null ? "탈퇴한 유저" : user.getUsername();
            return new UserReservationResponseDto(x, mrId, username);
        }).toList());
    }
}
