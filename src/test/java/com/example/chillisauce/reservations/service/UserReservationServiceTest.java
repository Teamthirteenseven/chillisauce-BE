package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.repository.ReservationUserRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        void 유저의_예약내역을_반환한다() {
            // given

            // when

            // then

        }
    }

}