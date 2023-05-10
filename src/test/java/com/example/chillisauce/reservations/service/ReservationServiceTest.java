package com.example.chillisauce.reservations.service;

import com.example.chillisauce.fixture.FixtureFactory;
import com.example.chillisauce.reservations.dto.request.ReservationAttendee;
import com.example.chillisauce.reservations.dto.request.ReservationRequest;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.dto.response.ReservationListResponse;
import com.example.chillisauce.reservations.dto.response.ReservationResponse;
import com.example.chillisauce.reservations.dto.response.ReservationTimetableResponse;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.repository.ReservationUserRepository;
import com.example.chillisauce.reservations.vo.ReservationTimetable;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.repository.LocationRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.NestedTestConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    LocationRepository locationRepository;
    @InjectMocks
    ReservationService reservationService;
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    ReservationUserRepository reservationUserRepository;
    @Mock
    private CompanyRepository companyRepository;

    @Nested
    @DisplayName("getAllReservations 메서드는")
    class GetAllReservationsTestCase {
        // given
        Integer offset = 0;
        Companies company = Company_생성();
        Mr meetingRoom = MeetingRoom_생성_아이디_지정(1L);
        User user = User_USER권한_생성(company);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        Reservation reservationOne = Reservation_생성_아이디_지정(1L, user, meetingRoom,
                LocalDateTime.of(2023, 4, 11, 15, 0),
                LocalDateTime.of(2023, 4, 11, 15, 59));

        Reservation reservationTwo = Reservation_생성_아이디_지정(2L, user, meetingRoom,
                LocalDateTime.of(2023, 4, 11, 17, 0),
                LocalDateTime.of(2023, 4, 11, 17, 59));

        List<Reservation> reservations = List.of(reservationOne, reservationTwo);
        Page<Reservation> reservationPage = new PageImpl<>(reservations);

        @Test
        void 회사_전체_예약내역을_조회한다() {
            // given
            Pageable pageable = PageRequest.of(offset, 20);

            when(reservationRepository
                    .findAllByCompanyName(eq(company.getCompanyName()), any()))
                    .thenReturn(reservationPage);

            // when
            ReservationListResponse result = reservationService
                    .getAllReservations(company.getCompanyName(), offset, userDetails);

            // then
            assertThat(result.getReservationList().size()).isEqualTo(2);
            assertThat(result.getReservationList()).extracting("reservationId", Long.class).contains(1L, 2L);
        }

        @Nested
        @DisplayName("다른 회사 유저가 접근할 경우")
        class InvalidUserCase{
            // given
            Companies companyTwo = Company_생성_이름_지정("anotherCompany");
            User userTwo = User_USER권한_생성(companyTwo);
            UserDetailsImpl userDetails = new UserDetailsImpl(userTwo, userTwo.getEmail());
            @Test
            void 접근_불가_예외를_반환한다(){
                // when, then
                assertThatThrownBy(()-> reservationService.getAllReservations(company.getCompanyName(), offset, userDetails))
                        .isInstanceOf(ReservationException.class).hasMessage("예약에 접근할 수 없는 유저입니다.");
            }
        }
    }

    @Nested
    @DisplayName("getReservationTimetable 메서드는")
    class GetReservationTimetableTestCase {
        // given
        Companies company = Company_생성();
        Mr meetingRoom = MeetingRoom_생성_아이디_지정(1L);
        User user = User_USER권한_생성(company);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());

        @Test
        void 특정_회의실_특정_날짜의_예약타임테이블을_조회한다() {
            // given
            LocalDate selDate = LocalDateTime.now().toLocalDate().plusDays(1L);
            Reservation reservationOne = Reservation_생성_아이디_지정(1L, user, meetingRoom,
                    LocalDateTime.of(selDate, LocalTime.of(15, 0, 0)),
                    LocalDateTime.of(selDate, LocalTime.of(15, 59, 0)));

            Reservation reservationTwo = Reservation_생성_아이디_지정(2L, user, meetingRoom,
                    LocalDateTime.of(selDate, LocalTime.of(17, 0, 0)),
                    LocalDateTime.of(selDate, LocalTime.of(17, 59, 0)));

            when(locationRepository.findByIdAndCompanyName(meetingRoom.getId(), company.getCompanyName()))
                    .thenReturn(Optional.of(meetingRoom));
            when(reservationRepository
                    .findAllByMeetingRoomIdAndStartTimeBetween(meetingRoom.getId(),
                            selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX)))
                    .thenReturn(List.of(reservationOne, reservationTwo));

            // when
            ReservationTimetableResponse result =
                    reservationService.getReservationTimetable(selDate, meetingRoom.getId(), userDetails);

            // then
            assertThat(result.getTimeList()).isNotEmpty()
                    .hasSize(ReservationTimetable.CLOSE_HOUR - ReservationTimetable.OPEN_HOUR + 1)
                    .filteredOn(x -> x.getIsCheckOut().equals(true))
                    .hasSize(2);
        }

        @Test
        void 오늘_이전의_날짜를_고르면_예약_불가를_반환한다() {
            // given
            LocalDate selDate = LocalDateTime.now().toLocalDate().minusDays(1L);

            when(locationRepository.findByIdAndCompanyName(meetingRoom.getId(), company.getCompanyName()))
                    .thenReturn(Optional.of(meetingRoom));

            // when
            ReservationTimetableResponse result = reservationService
                    .getReservationTimetable(selDate, meetingRoom.getId(), userDetails);

            // then
            assertThat(result.getTimeList())
                    .filteredOn(x -> x.getIsCheckOut().equals(true))
                    .hasSize(ReservationTimetable.CLOSE_HOUR - ReservationTimetable.OPEN_HOUR + 1);
        }

        @Nested
        @DisplayName("해당하는 회의실이 없으면")
        class NotFoundCase{
            // given
            LocalDate selDate = LocalDateTime.now().toLocalDate().plusDays(1L);
            @Test
            void 예외를_반환한다(){
                // given
                when(locationRepository.findByIdAndCompanyName(eq(meetingRoom.getId()), eq(company.getCompanyName())))
                        .thenReturn(Optional.empty());

                // when, then
                assertThatThrownBy(()-> reservationService
                        .getReservationTimetable(selDate, meetingRoom.getId(), userDetails))
                        .isInstanceOf(ReservationException.class).hasMessage("등록된 회의실이 없습니다.");
            }
        }

        @Nested
        @DisplayName("회의실이 아니면")
        class TypeErrorCase{
            // given
            Box box = Box_생성();
            LocalDate selDate = LocalDateTime.now().toLocalDate().plusDays(1L);
            @Test
            void 예외를_반환한다(){
                // given
                when(locationRepository.findByIdAndCompanyName(eq(meetingRoom.getId()), eq(company.getCompanyName())))
                        .thenReturn(Optional.of(box));

                // when, then
                assertThatThrownBy(()-> reservationService
                        .getReservationTimetable(selDate, meetingRoom.getId(), userDetails))
                        .isInstanceOf(ReservationException.class).hasMessage("해당 장소는 회의실이 아닙니다.");
            }
        }
    }

    @Nested
    @DisplayName("addReservation 메서드는")
    class AddReservationTestCase {
        // given
        Companies company = Company_생성();
        User organizer = User_USER권한_생성_아이디지정(1L, company);
        User attendee = User_USER권한_생성_아이디_이메일_지정(2L, company, "test2@test.com");
        UserDetailsImpl userDetails = new UserDetailsImpl(organizer, organizer.getEmail());
        Mr meetingRoom = MeetingRoom_생성_아이디_지정(1L);
        ReservationTime selectTime = new ReservationTime(LocalDateTime.of(2023, 4, 8, 12, 0));
        List<ReservationTime> startList = List.of(selectTime);
        List<ReservationAttendee> userList = List.of(new ReservationAttendee(organizer.getId()), new ReservationAttendee(attendee.getId()));
        ReservationRequest requestDto = new ReservationRequest(startList, userList);

        @Test
        void 예약을_등록한다() {
            // given
            when(locationRepository.findByIdAndCompanyName(eq(meetingRoom.getId()), eq(company.getCompanyName())))
                            .thenReturn(Optional.of(meetingRoom));

            // when
            ReservationResponse result = reservationService.addReservation(meetingRoom.getId(), requestDto, userDetails);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStart()).isEqualTo(selectTime.getStart());
            assertThat(result.getEnd()).isEqualTo(selectTime.getStart().plusMinutes(59));
        }


        @Test
        void 중복되는_시간이_있으면_예외가_발생한다() {
            // given
            ReservationTime secondSelectTime = new ReservationTime(LocalDateTime.of(2023, 4, 8, 12, 0));
            List<ReservationTime> list = List.of(secondSelectTime);
            Reservation firstReservation = Reservation_생성(organizer, meetingRoom,
                    secondSelectTime.getStart(), secondSelectTime.getStart().plusMinutes(59));

            ReservationRequest secondReservationDto = new ReservationRequest(list, userList);

            when(locationRepository.findByIdAndCompanyName(eq(meetingRoom.getId()), eq(company.getCompanyName())))
                    .thenReturn(Optional.of(meetingRoom));

            doReturn(Optional.of(firstReservation)).when(reservationRepository)
                    .findFirstByMeetingRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(meetingRoom.getId(),
                            selectTime.getStart(), selectTime.getStart().plusMinutes(59));

            // when
            final ReservationException exception = assertThrows(ReservationException.class,
                    () -> reservationService.addReservation(meetingRoom.getId(), secondReservationDto, userDetails));

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("해당 시간대에 이미 등록된 예약이 있습니다.");
        }

        @Nested
        @DisplayName("해당하는 회의실이 없으면")
        class NotFoundCase{
            @Test
            void 예외를_반환한다(){
                // given
                when(locationRepository.findByIdAndCompanyName(eq(meetingRoom.getId()), eq(company.getCompanyName())))
                        .thenReturn(Optional.empty());

                // when, then
                assertThatThrownBy(()-> reservationService
                        .addReservation(meetingRoom.getId(), requestDto, userDetails))
                        .isInstanceOf(ReservationException.class).hasMessage("등록된 회의실이 없습니다.");
            }
        }

        @Nested
        @DisplayName("회의실이 아니면")
        class TypeErrorCase{
            // given
            Box box = Box_생성();
            @Test
            void 예외를_반환한다(){
                // given
                when(locationRepository.findByIdAndCompanyName(eq(meetingRoom.getId()), eq(company.getCompanyName())))
                        .thenReturn(Optional.of(box));

                // when, then
                assertThatThrownBy(()-> reservationService
                        .addReservation(meetingRoom.getId(), requestDto, userDetails))
                        .isInstanceOf(ReservationException.class).hasMessage("해당 장소는 회의실이 아닙니다.");
            }
        }
    }

    @Nested
    @DisplayName("editReservation 메서드는")
    class EditReservationTestCase {
        // given
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        Mr meetingRoom = MeetingRoom_생성_아이디_지정(1L);
        Reservation before = Reservation_생성_아이디_지정(1L, user, meetingRoom,
                LocalDateTime.of(2023,4,8,11,0),
                LocalDateTime.of(2023,4,8,13,59));

        @Test
        void 예약을_수정한다() {
            // given
            ReservationTime selectTime = new ReservationTime(LocalDateTime.of(2023, 4,8,12,0));
            List<ReservationTime> startList = List.of(selectTime);
            ReservationAttendee userOne = new ReservationAttendee(1L);
            ReservationAttendee userTwo = new ReservationAttendee(2L);
            List<ReservationAttendee> userList = List.of(userOne, userTwo);
            ReservationRequest request = new ReservationRequest(startList, userList);

            // when
            when(reservationRepository.findById(eq(before.getId()))).thenReturn(Optional.of(before));

            ReservationResponse result = reservationService.editReservation(before.getId(), request, userDetails);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStart()).isEqualTo(selectTime.getStart());
            assertThat(result.getEnd()).isEqualTo(selectTime.getStart().plusMinutes(59));
        }
    }

    @Nested
    @DisplayName("deleteReservation 메서드는")
    class DeleteReservationTestCase {
        // given
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        Mr meetingRoom = MeetingRoom_생성_아이디_지정(1L);
        Reservation target = Reservation_생성_아이디_지정(1L, user, meetingRoom,
                LocalDateTime.of(2023, 5, 3, 17, 0),
                LocalDateTime.of(2023,5,3,17,59));

        @Test
        void 예약을_삭제한다() {
            // when
            when(reservationRepository.findById(eq(target.getId()))).thenReturn(Optional.of(target));

            String result = reservationService.deleteReservation(target.getId(), userDetails);

            // then
            assertThat(result).isEqualTo("success");
        }

        @Test
        void 예약이_없으면_예외가_발생한다(){
            // given
            when(reservationRepository.findById(eq(target.getId()))).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(()-> reservationService.deleteReservation(target.getId(), userDetails))
                    .isInstanceOf(ReservationException.class).hasMessage("예약을 찾을 수 없습니다.");
        }

        @Test
        void 다른_유저가_요청하면_권한_예외가_발생한다(){
            // given
            User another = User_USER권한_생성_아이디_이메일_지정(2L, company, "test2@test.com");
            UserDetailsImpl anotherDetails = new UserDetailsImpl(another, another.getEmail());
            when(reservationRepository.findById(eq(target.getId()))).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(()->reservationService.deleteReservation(target.getId(), anotherDetails))
                    .isInstanceOf(ReservationException.class).hasMessage("예약을 찾을 수 없습니다.");
        }
    }
}