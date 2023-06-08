package com.example.chillisauce.domain.reservations.service;

import com.example.chillisauce.domain.reservations.dto.response.UserReservationListResponse;
import com.example.chillisauce.domain.reservations.entity.Reservation;
import com.example.chillisauce.domain.reservations.entity.ReservationUser;
import com.example.chillisauce.domain.reservations.repository.ReservationRepository;
import com.example.chillisauce.domain.reservations.repository.ReservationUserRepository;
import com.example.chillisauce.domain.reservations.service.UserReservationService;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.spaces.entity.Mr;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserReservationService 클래스")
class UserReservationServiceTest {

    @InjectMocks
    UserReservationService userReservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    ReservationUserRepository reservationUserRepository;

    @Nested
    @DisplayName("getUserReservations 메서드는")
    class GetUserReservationsTestCase {
        // given
        Companies company = Company_생성();
        User organizer = User_USER권한_생성_아이디지정(1L, company);
        UserDetailsImpl userDetails = new UserDetailsImpl(organizer, organizer.getEmail());
        Mr meetingRoomOne = MeetingRoom_생성_아이디_지정(1L);
        Mr meetingRoomTwo = MeetingRoom_생성_아이디_지정(2L);
        Reservation reservationOne = Reservation_생성_아이디_지정(1L, organizer, meetingRoomOne,
                LocalDateTime.of(2023, 5, 3, 11, 0),
                LocalDateTime.of(2023, 5, 3, 11, 59));

        Reservation reservationTwo = Reservation_생성_아이디_지정(2L, organizer, meetingRoomTwo,
                LocalDateTime.of(2023, 5, 3, 15, 0),
                LocalDateTime.of(2023, 5, 3, 15, 59));

        User attendeeOne = User_USER권한_생성_아이디지정(2L, company);
        User attendeeTwo = User_USER권한_생성_아이디지정(3L, company);
        ReservationUser reservationUserOne = ReservationUser_생성_아이디_지정(reservationOne, organizer, 1L);
        ReservationUser reservationUserTwo = ReservationUser_생성_아이디_지정(reservationOne, attendeeOne, 2L);
        ReservationUser reservationUserThree = ReservationUser_생성_아이디_지정(reservationOne, organizer, 3L);
        ReservationUser reservationUserFour = ReservationUser_생성_아이디_지정(reservationOne, attendeeTwo, 4L);
        @Test
        void 유저의_예약내역을_반환한다() {
            // given
            when(reservationRepository.findAllByUserId(eq(organizer.getId())))
                    .thenReturn(List.of(reservationOne, reservationTwo));

            // when
            UserReservationListResponse result = userReservationService.getUserReservations(userDetails);

            // then
            assertThat(result.getReservationList().size()).isEqualTo(2);
        }
    }

}