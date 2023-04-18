package com.example.chillisauce.schedules.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleService 클래스")
class ScheduleServiceTest {

    @Nested
    @DisplayName("getAllSchedules 메서드는")
    class GetAllSchedulesTestCase {
        @Test
        void 회원의_전체_스케줄을_조회한다(){

        }
    }

    @Nested
    @DisplayName("getDaySchedules 메서드는")
    class GetDaySchedulesTestCase{
        @Test
        void 당일_스케줄을_조회한다(){

        }
    }

    @Nested
    @DisplayName("addSchedule 메서드는")
    class AddScheduleTestCase{
        @Test
        void 스케줄을_등록한다(){

        }

        @Test
        void 시간이_겹치면_예외가_발생한다(){

        }
    }

    @Nested
    @DisplayName("editSchedule 메서드는")
    class EditScheduleTestCase{
        @Test
        void 스케줄을_수정한다(){

        }

        @Test
        void 시간이_겹치면_예외가_발생한다(){

        }

        @Test
        void 권한이_없으면_예외가_발생한다(){

        }

        @Test
        void 스케줄이_없으면_예외가_발생한다(){

        }
    }

    @Nested
    @DisplayName("deleteSchedule 메서드는")
    class DeleteScheduleTestCase{
        @Test
        void 스케줄을_삭제한다(){

        }
        @Test
        void 권한이_없으면_예외가_발생한다(){

        }

        @Test
        void 스케줄이_없으면_예외가_발생한다(){

        }
    }
}