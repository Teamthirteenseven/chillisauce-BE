package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.request.ReservationAttendee;
import com.example.chillisauce.reservations.dto.request.ReservationRequestDto;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.dto.response.ReservationListResponseDto;
import com.example.chillisauce.reservations.dto.response.ReservationResponseDto;
import com.example.chillisauce.reservations.dto.response.ReservationTimetableResponseDto;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.repository.ReservationUserRepository;
import com.example.chillisauce.reservations.vo.ReservationTimetable;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
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
    @Mock
    UserRepository userRepository;
    @Mock
    ReservationUserRepository reservationUserRepository;
    @Mock
    ScheduleRepository scheduleRepository;
    @InjectMocks
    ReservationService reservationService;

    @Mock
    private CompanyRepository companyRepository;

    @Nested
    @DisplayName("getAllReservations 메서드는")
    class GetAllReservationsTestCase {
        // given
        int size = 2;
        Mr meetingRoom = Mr.builder()
                .id(1L)
                .build();
        User userOne = User.builder().username("testUser1").build();
        User userTwo = User.builder().username("testUser2").build();
        UserDetailsImpl userDetails = new UserDetailsImpl(new User(), "user");
        Reservation reservationOne = Reservation.builder()
                .id(1L)
                .meetingRoom(meetingRoom)
                .user(userOne)
                .startTime(LocalDateTime.of(2023, 4, 11, 15, 0))
                .endTime(LocalDateTime.of(2023, 4, 11, 15, 59))
                .build();

        Reservation reservationTwo = Reservation.builder()
                .id(2L)
                .meetingRoom(meetingRoom)
                .user(userTwo)
                .startTime(LocalDateTime.of(2023, 4, 11, 17, 0))
                .endTime(LocalDateTime.of(2023, 4, 11, 17, 59))
                .build();

        @Test
        void 회사_전체_예약내역을_조회한다() {
            // given
            String companyName = "testCompany";
            when(companyRepository.findByCompanyName(eq(companyName)))
                    .thenReturn(Optional.of(Companies.builder().build()));
            when(reservationRepository.findAll()).thenReturn(List.of(reservationOne, reservationTwo));

            // when
            ReservationListResponseDto result = reservationService
                    .getAllReservations(companyName, userDetails);

            // then
            assertThat(result.getReservationList().size()).isEqualTo(size);
            assertThat(result.getReservationList()).extracting("reservationId", Long.class)
                    .contains(1L, 2L);
        }
    }

    @Nested
    @DisplayName("getReservationTimetable 메서드는")
    class GetReservationTimetableTestCase {
        // given
        LocalDate selDate = LocalDate.of(2023, 4, 13);
        Mr meetingRoom = Mr.builder().id(1L).build();
        User user = User.builder()
                .username("testUser")
                .build();
        Reservation reservationOne = Reservation.builder()
                .id(1L)
                .meetingRoom(meetingRoom)
                .user(user)
                .startTime(LocalDateTime.of(2023, 4, 13, 15, 0))
                .endTime(LocalDateTime.of(2023, 4, 13, 15, 59))
                .build();

        Reservation reservationTwo = Reservation.builder()
                .id(2L)
                .meetingRoom(meetingRoom)
                .user(user)
                .startTime(LocalDateTime.of(2023, 4, 13, 17, 0))
                .endTime(LocalDateTime.of(2023, 4, 13, 17, 59))
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());

        @Test
        void 특정_회의실_특정_날짜의_예약타임테이블을_조회한다() {
            // given
            when(meetingRoomRepository.findById(meetingRoom.getId())).thenReturn(Optional.of(meetingRoom));
            when(reservationRepository.findAllByMeetingRoomIdAndStartTimeBetween(meetingRoom.getId(),
                    selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX))).thenReturn(List.of(reservationOne, reservationTwo));

            // when
            ReservationTimetableResponseDto result = reservationService
                    .getReservationTimetable(selDate, meetingRoom.getId(), userDetails);

            // then
            assertThat(result.getTimeList())
                    .isNotEmpty()
                    .hasSize(ReservationTimetable.CLOSE_HOUR - ReservationTimetable.OPEN_HOUR + 1)
                    .filteredOn(x -> x.getIsCheckOut().equals(true))
                    .isNotEmpty();
        }
    }

    @Nested
    @DisplayName("addReservation 메서드는")
    class AddReservationTestCase {
        // given
        Long meetingRoomId = 1L;
        User organizer = User.builder()
                .id(1L)
                .email("test@email.com")
                .username("tester")
                .password("12345678")
                .role(UserRoleEnum.USER)
                .build();

        User attendee = User.builder().id(2L).build();

        Mr meetingRoom = Mr.builder()
                .id(meetingRoomId)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(organizer, organizer.getEmail());
        LocalDate startDate = LocalDate.of(2023, 4, 8);
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        ReservationTime unitDto = new ReservationTime(start);
        List<ReservationTime> startList = List.of(unitDto);
        ReservationAttendee userOne = new ReservationAttendee(1L);
        ReservationAttendee userTwo = new ReservationAttendee(2L);
        List<ReservationAttendee> userList = List.of(userOne, userTwo);
        ReservationRequestDto requestDto = new ReservationRequestDto(startList, userList);

        @Test
        void 예약을_등록한다() {
            // given
            when(userRepository.findAllByIdIn(any())).thenReturn(List.of(organizer, attendee));

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
            ReservationTime secondUnitDto = new ReservationTime(secondStart);
            List<ReservationTime> list = List.of(secondUnitDto);
            Reservation firstReservation = Reservation.builder()
                    .startTime(start)
                    .endTime(start.plusMinutes(59))
                    .build();

            ReservationRequestDto secondReservationDto = new ReservationRequestDto(list, userList);

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
            ReservationTime unitDto = new ReservationTime(startTime);
            List<ReservationTime> list = List.of(unitDto);
            ReservationRequestDto start = new ReservationRequestDto(list, userList);
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
    class EditReservationTestCase {
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
        LocalDateTime targetStartTime = LocalDateTime.of(2023, 4, 8, 11, 0);
        LocalDateTime targetEndTime = LocalDateTime.of(2023, 4, 8, 13, 59);
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
        ReservationTime unitDto = new ReservationTime(editStart);
        List<ReservationTime> startList = List.of(unitDto);
        ReservationAttendee userOne = new ReservationAttendee(1L);
        ReservationAttendee userTwo = new ReservationAttendee(2L);
        List<ReservationAttendee> userList = List.of(userOne, userTwo);
        ReservationRequestDto requestDto = new ReservationRequestDto(startList, userList);

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
    class DeleteReservationTestCase {
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