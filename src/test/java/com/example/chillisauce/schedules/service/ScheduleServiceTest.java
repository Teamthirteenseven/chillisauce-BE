package com.example.chillisauce.schedules.service;

import com.example.chillisauce.schedules.dto.*;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import org.assertj.core.api.Assertions;
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
        void 당일_스케줄을_조회한다() {
            // given
            when(scheduleRepository.findAllByUserIdAndStartTimeBetween(eq(1L), any(), any())).thenReturn(schedules);

            // when
            ScheduleTimetableResponseDto result = scheduleService.getDaySchedules(selDate, userDetails);

            // then
            assertThat(result).isNotNull();
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
                .scComment("test schedule somment")
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
        @Test
        void 스케줄을_수정한다() {

        }

        @Test
        void 권한이_없으면_예외가_발생한다() {

        }

        @Test
        void 스케줄이_없으면_예외가_발생한다() {

        }
    }

    @Nested
    @DisplayName("deleteSchedule 메서드는")
    class DeleteScheduleTestCase {
        @Test
        void 스케줄을_삭제한다() {

        }

        @Test
        void 권한이_없으면_예외가_발생한다() {

        }

        @Test
        void 스케줄이_없으면_예외가_발생한다() {

        }
    }
}