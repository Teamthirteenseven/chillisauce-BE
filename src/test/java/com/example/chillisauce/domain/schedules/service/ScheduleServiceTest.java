package com.example.chillisauce.domain.schedules.service;

import com.example.chillisauce.domain.reservations.vo.ReservationTimetable;
import com.example.chillisauce.domain.schedules.dto.*;
import com.example.chillisauce.domain.schedules.service.ScheduleService;
import com.example.chillisauce.domain.schedules.entity.Schedule;
import com.example.chillisauce.domain.schedules.exception.ScheduleException;
import com.example.chillisauce.domain.schedules.repository.ScheduleRepository;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.entity.User;
import com.example.chillisauce.domain.users.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleService 클래스")
class ScheduleServiceTest {

    @InjectMocks
    ScheduleService scheduleService;

    @Mock
    ScheduleRepository scheduleRepository;

    @Nested
    @DisplayName("getAllSchedules 메서드는")
    class GetAllSchedulesTestCase {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());
        Schedule scheduleOne = Schedule.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2023, 4, 26, 15, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 16, 0))
                .user(user)
                .title("testScheduleOne")
                .build();

        Schedule scheduleTwo = Schedule.builder()
                .id(2L)
                .startTime(LocalDateTime.of(2023, 4, 26, 19, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 20, 0))
                .user(user)
                .title("testScheduleTwo")
                .build();
        List<Schedule> schedules = List.of(scheduleOne, scheduleTwo);

        @Test
        void 회원의_전체_스케줄을_조회한다() {
            // given
            when(scheduleRepository.findAllByUserId(eq(user.getId()))).thenReturn(schedules);

            // when
            ScheduleListResponseDto result = scheduleService.getAllSchedules(userDetails);

            // then
            assertThat(result.getScList().size()).isEqualTo(2);
            assertThat(result.getScList()).extracting("scId", Long.class)
                    .contains(1L, 2L);
        }
    }

    @Nested
    @DisplayName("getDaySchedules 메서드는")
    class GetDaySchedulesTestCase {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());
        LocalDate selDate = LocalDate.of(2023, 4, 26);
        Schedule scheduleOne = Schedule.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2023, 4, 26, 15, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 15, 59))
                .user(user)
                .title("testScheduleOne")
                .build();

        Schedule scheduleTwo = Schedule.builder()
                .id(2L)
                .startTime(LocalDateTime.of(2023, 4, 26, 19, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 19, 59))
                .user(user)
                .title("testScheduleTwo")
                .build();
        List<Schedule> schedules = List.of(scheduleOne, scheduleTwo);

        @Test
        void 당일_스케줄을_조회한다() {
            // given
            when(scheduleRepository.findAllByUserIdAndStartTimeBetween(eq(1L), any(), any())).thenReturn(schedules);

            // when
            ScheduleTimetableResponseDto result = scheduleService.getDaySchedules(selDate, userDetails);

            // then
            assertThat(result.getTimeList())
                    .isNotEmpty()
                    .hasSize(ReservationTimetable.CLOSE_HOUR - ReservationTimetable.OPEN_HOUR + 1)
                    .filteredOn(x -> x.getIsCheckOut().equals(true))
                    .hasSize(2);
        }
    }

    @Nested
    @DisplayName("addSchedule 메서드는")
    class AddScheduleTestCase {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());
        ScheduleRequestDto requestOne = ScheduleRequestDto.builder()
                .scComment("test schedule comment")
                .scTitle("test schedule title")
                .startList(List.of(new ScheduleTime(LocalDateTime.of(2023, 4, 26, 15, 0))))
                .build();

        Schedule scheduleOne = Schedule.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2023, 4, 26, 15, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 16, 0))
                .user(user)
                .title("testScheduleOne")
                .build();

        @Test
        void 스케줄을_등록한다() {
            // given
            when(scheduleRepository.save(any())).thenReturn(scheduleOne);

            // when
            ScheduleResponseDto result = scheduleService.addSchedule(requestOne, userDetails);

            // then
            assertThat(result.getScStart()).isEqualTo(scheduleOne.getStartTime());
            assertThat(result.getScEnd()).isEqualTo(scheduleOne.getEndTime());
        }
    }

    @Nested
    @DisplayName("editSchedule 메서드는")
    class EditScheduleTestCase {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());
        Long scheduleId = 1L;
        ScheduleRequestDto request = ScheduleRequestDto.builder()
                .scComment("test schedule comment")
                .scTitle("test schedule title")
                .startList(List.of(new ScheduleTime(LocalDateTime.of(2023, 4, 26, 15, 0))))
                .build();
        Schedule scheduleOne = Schedule.builder()
                .id(scheduleId)
                .comment("before schedule comment")
                .title("before schedule title")
                .user(user)
                .startTime(LocalDateTime.of(2023, 4, 26, 17, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 17, 59))
                .build();

        @Test
        void 스케줄을_수정한다() {
            // given
            when(scheduleRepository.findById(eq(1L))).thenReturn(Optional.of(scheduleOne));

            // when
            ScheduleResponseDto result = scheduleService.editSchedule(scheduleId, request, userDetails);

            // then
            assertThat(result.getScStart()).isEqualTo(LocalDateTime.of(2023, 4, 26, 15, 0));
        }

        @Test
        void 스케줄이_없으면_예외가_발생한다() {
            // given
            Long wrongId = 2L;
            when(scheduleRepository.findById(eq(wrongId))).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(()-> scheduleService.editSchedule(wrongId, request, userDetails))
                    .isInstanceOf(ScheduleException.class).hasMessage("스케줄을 찾을 수 없습니다.");
        }

        @Test
        void 다른_유저가_요청하면_예외가_발생한다() {
            // given
            User another = User.builder()
                    .id(2L)
                    .email("test2@test.com")
                    .username("anotherUser")
                    .role(UserRoleEnum.USER)
                    .build();

            UserDetailsImpl anotherDetails = new UserDetailsImpl(another, another.getUsername());

            when(scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.of(scheduleOne));

            // when, then
            assertThatThrownBy(()-> scheduleService.editSchedule(scheduleId, request, anotherDetails))
                    .isInstanceOf(ScheduleException.class).hasMessage("스케줄을 수정할 권한이 없는 유저입니다.");
        }
    }

    @Nested
    @DisplayName("deleteSchedule 메서드는")
    class DeleteScheduleTestCase {
        // given
        Long scheduleId = 1L;
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("testUser")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());

        Schedule scheduleOne = Schedule.builder()
                .id(scheduleId)
                .comment("before schedule comment")
                .title("before schedule title")
                .user(user)
                .startTime(LocalDateTime.of(2023, 4, 26, 17, 0))
                .endTime(LocalDateTime.of(2023, 4, 26, 17, 59))
                .build();
        @Test
        void 스케줄을_삭제한다() {
            // given
            when(scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.of(scheduleOne));

            // when
            String result = scheduleService.deleteSchedule(scheduleId, userDetails);

            // then
            assertThat(result).isEqualTo("success");
        }

        @Test
        void 다른_유저가_요청하면_예외가_발생한다() {
            // given
            Companies company = Company_생성();
            User another = User_USER권한_생성_아이디_이메일_지정(2L, company, "test2@test.com");

            UserDetailsImpl anotherDetails = new UserDetailsImpl(another, another.getUsername());

            when(scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.of(scheduleOne));

            // when, then
            assertThatThrownBy(()-> scheduleService.deleteSchedule(scheduleId, anotherDetails))
                    .isInstanceOf(ScheduleException.class).hasMessage("스케줄을 삭제할 권한이 없는 유저입니다.");
        }

        @Test
        void 스케줄이_없으면_예외가_발생한다() {
            // when
            when(scheduleRepository.findById(eq(scheduleId))).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> scheduleService.deleteSchedule(scheduleId, userDetails))
                    .isInstanceOf(ScheduleException.class).hasMessage("스케줄을 찾을 수 없습니다.");
        }
    }
}