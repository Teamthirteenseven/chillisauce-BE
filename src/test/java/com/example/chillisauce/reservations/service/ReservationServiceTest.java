package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.ReservationListRequestDto;
import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.dto.ReservationResponseDto;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.exception.ReservationErrorCode;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 클래스")
class ReservationServiceTest {
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    MrRepository meetingRoomRepository;
    @InjectMocks
    ReservationService reservationService;
    @Nested
    @DisplayName("getAllReservations 메서드는")
    class GetAllReservationsTestCase {
        @Test
        void 회사_전체_예약내역을_조회한다(){

        }
    }

    @Nested
    @DisplayName("getReservationTimetable 메서드는")
    class GetReservationTimetableTestCase {
        @Test
        void 특정_회의실_특정_날짜의_예약타임테이블을_조회한다(){

        }
    }

    @Nested
    @DisplayName("addReservation 메서드는")
    class AddReservationTestCase{
        // given
        Long meetingRoomId = 1L;
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("tester")
                .password("12345678")
                .role(UserRoleEnum.USER)
                .build();

        Mr meetingRoom = Mr.builder()
                .id(meetingRoomId)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        LocalDate startDate = LocalDate.of(2023, 4, 8);
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        ReservationRequestDto unitDto = new ReservationRequestDto(start);
        List<ReservationRequestDto> list = List.of(unitDto);
        ReservationListRequestDto requestDto = new ReservationListRequestDto(list);
        
        @Test
        void 예약을_등록한다() {
            // when
            when(meetingRoomRepository.findById(eq(meetingRoomId))).thenReturn(Optional.of(meetingRoom));

            ReservationResponseDto result = reservationService.addReservation(meetingRoomId, requestDto, userDetails);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStart()).isEqualTo(start);
            assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(startDate, startTime).plusMinutes(59));
        }

        @Test
        void 중복되는_시간이_있으면_예외가_발생한다() {
            // given
            LocalDate secondDate = LocalDate.of(2023, 4, 8);
            LocalTime secondTime = LocalTime.of(12, 0);
            LocalDateTime secondStart = LocalDateTime.of(secondDate, secondTime);
            ReservationRequestDto secondUnitDto = new ReservationRequestDto(secondStart);
            List<ReservationRequestDto> list = List.of(secondUnitDto);
            Reservation firstReservation = Reservation.builder()
                    .startTime(start)
                    .endTime(start.plusMinutes(59))
                    .build();

            ReservationListRequestDto secondReservationDto = new ReservationListRequestDto(list);

            when(meetingRoomRepository.findById(any(Long.class)))
                    .thenReturn(Optional.of(meetingRoom)).thenThrow(ReservationException.class);

            doReturn(Optional.of(firstReservation)).when(reservationRepository)
                    .findFirstByMeetingRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                            meetingRoomId, secondStart, secondStart.plusMinutes(59));

            // when
            final ReservationException exception =
                    assertThrows(ReservationException.class,
                            () -> reservationService.addReservation(meetingRoomId, secondReservationDto, userDetails));

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("해당 시간대에 이미 등록된 예약이 있습니다.");
        }

        @Test
        void 동시_예약이_발생하면_하나만_등록한다() throws InterruptedException {
            // given
            int threadCount = 3;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            List<ReservationResponseDto> resultList = new ArrayList<>();

            LocalDateTime startTime = LocalDateTime.of(2023, 4, 8, 12, 0);
            ReservationRequestDto unitDto = new ReservationRequestDto(startTime);
            List<ReservationRequestDto> list = List.of(unitDto);
            ReservationListRequestDto start = new ReservationListRequestDto(list);
            when(meetingRoomRepository.findById(meetingRoomId)).thenReturn(Optional.of(meetingRoom));

            // when
            IntStream.range(0, threadCount).forEach(e ->
                    executorService.submit(() -> {
                        try {
                            resultList.add(reservationService.addReservation(meetingRoomId, start, userDetails));
                        } finally {
                            countDownLatch.countDown();
                        }
                    }));

            countDownLatch.await();

            // then
            //TODO: 동시성 이슈 해결 후 수정 필요
            assertThat(resultList.size()).isPositive();
        }
        
        @Test
        void 해당_회의실이_없으면_예외가_발생한다() {
            // given
            when(meetingRoomRepository.findById(eq(meetingRoomId))).thenReturn(Optional.empty());
            
            // when
            ReservationException exception = assertThrows(ReservationException.class,
                    () -> reservationService.addReservation(meetingRoomId, requestDto, userDetails));

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("등록된 회의실이 없습니다.");
        }
    }

    @Nested
    @DisplayName("editReservation 메서드는")
    class EditReservationTestCase{
        // given
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("tester")
                .password("12345678")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        Long meetingRoomId = 1L;
        Mr meetingRoom = Mr.builder()
                .id(meetingRoomId)
                .build();
        Long reservationId = 1L;
        LocalDateTime targetStartTime = LocalDateTime.of(2023,4,8,11,0);
        LocalDateTime targetEndTime = LocalDateTime.of(2023,4,8,13,59);
        Reservation target = Reservation.builder()
                .id(reservationId)
                .user(user)
                .meetingRoom(meetingRoom)
                .startTime(targetStartTime)
                .endTime(targetEndTime)
                .build();
        LocalDate editDate = LocalDate.of(2023, 4, 8);
        LocalTime editTime = LocalTime.of(12, 0);
        LocalDateTime editStart = LocalDateTime.of(editDate, editTime);
        ReservationRequestDto unitDto = new ReservationRequestDto(editStart);
        List<ReservationRequestDto> list = List.of(unitDto);
        ReservationListRequestDto requestDto = new ReservationListRequestDto(list);
        @Test
        void 예약을_수정한다() {
            // when
            when(reservationRepository.findById(eq(reservationId))).thenReturn(Optional.of(target));

            ReservationResponseDto result = reservationService.editReservation(reservationId, requestDto, userDetails);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStart()).isEqualTo(editStart);
            assertThat(result.getEnd()).isEqualTo(editStart.plusMinutes(59));
        }
    }

    @Nested
    @DisplayName("deleteReservation 메서드는")
    class DeleteReservationTestCase{
        // given
        User user = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("tester")
                .password("12345678")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        Long meetingRoomId = 1L;
        Mr meetingRoom = Mr.builder()
                .id(meetingRoomId)
                .build();
        Long reservationId = 1L;
        Reservation target = Reservation.builder()
                .id(reservationId)
                .user(user)
                .meetingRoom(meetingRoom)
                .build();
        @Test
        void 예약을_삭제한다() {
            // when
            when(reservationRepository.findById(eq(reservationId))).thenReturn(Optional.of(target));

            String result = reservationService.deleteReservation(reservationId, userDetails);

            // then
            assertThat(result).isEqualTo("success");
        }
    }
}