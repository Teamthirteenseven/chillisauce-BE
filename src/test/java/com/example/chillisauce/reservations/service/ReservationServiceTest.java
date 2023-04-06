package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.spaces.MrRepository;
import com.example.chillisauce.users.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    ReservationRepository reservationRepository;

    @Mock
    MrRepository meetingRoomRepository;
    @InjectMocks
    ReservationService reservationService;

    @Test
    void 예약등록실패_시간_중복() {
        // given
        Long meetingRoomId = 1L;
        User user = new User();

        LocalDateTime firstStart = LocalDateTime.of(2023, 4, 3, 12, 30);
        LocalDateTime firstEnd = LocalDateTime.of(2023, 4, 3, 15, 30);
        LocalDateTime secondStart = LocalDateTime.of(2023, 4, 3, 10, 30);
        LocalDateTime secondEnd = LocalDateTime.of(2023, 4, 3, 14, 30);
        ReservationRequestDto firstReservation = new ReservationRequestDto();
        ReservationRequestDto secondReservation = new ReservationRequestDto();
        firstReservation.setStart(firstStart);
        firstReservation.setEnd(firstEnd);
        secondReservation.setStart(secondStart);
        secondReservation.setEnd(secondEnd);

        // when
//        when(meetingRoomRepository.findById(any(Long.class)))
//        .thenReturn(Optional.of(new Mr()))
//                .thenThrow(ReservationException.class);
//
//        when(reservationRepository.findFirstByMeetingRoomAndStartTimeLessThanAndEndTimeGreaterThan(
//                any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
//                .thenReturn(Optional.of(new Reservation()));
//        reservationService.addReservation(meetingRoomId, secondReservation, any(UserDetailsImpl.class));

        // then

    }
}