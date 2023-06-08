package com.example.chillisauce.domain.schedules.controller;

import com.example.chillisauce.domain.schedules.controller.ScheduleController;
import com.example.chillisauce.domain.schedules.dto.*;
import com.example.chillisauce.domain.schedules.exception.ScheduleExceptionHandler;
import com.example.chillisauce.domain.schedules.service.ScheduleService;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@DisplayName("ReservationController 클래스")
class ScheduleControllerTest {
    @InjectMocks
    private ScheduleController scheduleController;

    @Mock
    private ScheduleService scheduleService;

    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(scheduleController)
                .setControllerAdvice(new ScheduleExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("전체 스케줄 GET 요청이 들어올 때")
    class GetAllSchedulesTestCase {
        // given
        String url = "/schedules/all";
        ScheduleListResponseDto all = getAllScheduleResponse();

        @Test
        @WithMockUser
        void 회원의_전체_스케줄을_응답한다() throws Exception {
            when(scheduleService.getAllSchedules(any())).thenReturn(all);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("get-all-schedules",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.scList").type(JsonFieldType.ARRAY).description("일정목록"),
                                    fieldWithPath("data.scList[].scId").type(JsonFieldType.NUMBER).description("예약 id"),
                                    fieldWithPath("data.scList[].scTitle").type(JsonFieldType.STRING).description("일정 제목"),
                                    fieldWithPath("data.scList[].scComment").type(JsonFieldType.STRING).description("일정 세부내용"),
                                    fieldWithPath("data.scList[].scStart").type(JsonFieldType.STRING).description("일정 시작 시각"),
                                    fieldWithPath("data.scList[].scEnd").type(JsonFieldType.STRING).description("일정 종료 시각")
                            )
                    ));
        }

        private ScheduleListResponseDto getAllScheduleResponse() {
            List<ScheduleResponseDto> responseList = new ArrayList<>();
            ScheduleResponseDto scheduleOne = ScheduleResponseDto.builder()
                    .scId(1L)
                    .scTitle("문서 정리하기")
                    .scComment("지난 주 공문서 정리")
                    .scStart(LocalDateTime.of(2023, 4, 18, 13, 0))
                    .scEnd(LocalDateTime.of(2023, 4, 18, 13, 30))
                    .build();

            ScheduleResponseDto scheduleTwo = ScheduleResponseDto.builder()
                    .scId(2L)
                    .scTitle("주간 회의 참석하기")
                    .scComment("회의 내용 기록 준비할 것")
                    .scStart(LocalDateTime.of(2023, 4, 18, 17, 0))
                    .scEnd(LocalDateTime.of(2023, 4, 18, 18, 0))
                    .build();

            responseList.add(scheduleOne);
            responseList.add(scheduleTwo);
            return new ScheduleListResponseDto(responseList);
        }
    }

    @Nested
    @DisplayName("당일 스케줄 GET 요청이 들어올 때")
    class GetDaySchedulesTestCase {
        // given
        String url = "/schedules";
        LocalDate selDate = LocalDate.of(2023, 4, 13);

        @Test
        @WithMockUser
        void 스케줄_타임테이블을_응답한다() throws Exception {
            // given
            ScheduleTimetableResponseDto timeTable = getScheduleTimetable();
            when(scheduleService.getDaySchedules(eq(selDate), any())).thenReturn(timeTable);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .param("selDate", "2023-04-13")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("get-schedule-timetable",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.timeList").type(JsonFieldType.ARRAY).description("타임테이블"),
                                    fieldWithPath("data.timeList[].isCheckOut").type(JsonFieldType.BOOLEAN).description("일정 등록 여부"),
                                    fieldWithPath("data.timeList[].start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.timeList[].end").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }

        private ScheduleTimetableResponseDto getScheduleTimetable() {
            ScheduleTimeResponseDto timeOne = ScheduleTimeResponseDto.builder().isCheckOut(false)
                    .start(LocalTime.of(7, 0))
                    .end(LocalTime.of(7, 59))
                    .build();

            ScheduleTimeResponseDto timeTwo = ScheduleTimeResponseDto.builder().isCheckOut(true)
                    .start(LocalTime.of(8, 0))
                    .end(LocalTime.of(8, 59))
                    .build();

            ScheduleTimeResponseDto timeLast = ScheduleTimeResponseDto.builder()
                    .isCheckOut(false)
                    .start(LocalTime.of(22, 0))
                    .end(LocalTime.of(22, 59))
                    .build();

            return new ScheduleTimetableResponseDto(List.of(timeOne, timeTwo, timeLast));
        }
    }

    @Nested
    @DisplayName("스케줄 POST 요청이 들어올 때")
    class AddSchedulesTestCase {
        // given
        String url = "/schedules";
        ScheduleTime selTimeOne = new ScheduleTime(LocalDateTime.of(2023, 4, 10, 13, 0));
        ScheduleTime selTimeTwo = new ScheduleTime(LocalDateTime.of(2023, 4, 10, 15, 0));
        String scTitle = "스케줄 제목";
        String scComment = "스케줄 내용";
        ScheduleRequestDto request = new ScheduleRequestDto(scTitle, scComment, List.of(selTimeOne, selTimeTwo));

        @Test
        void 스케줄을_등록한다() throws Exception {
            // given
            ScheduleResponseDto response = ScheduleResponseDto.builder()
                    .scTitle(scTitle)
                    .scComment(scComment)
                    .scId(1L)
                    .scStart(selTimeOne.getStart())
                    .scEnd(selTimeTwo.getStart())
                    .build();

            when(scheduleService.addSchedule(any(ScheduleRequestDto.class), any(UserDetailsImpl.class))).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("post-schedule",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("scTitle").type(JsonFieldType.STRING).description("일정 제목"),
                                    fieldWithPath("scComment").type(JsonFieldType.STRING).description("일정 세부내용"),
                                    fieldWithPath("startList").type(JsonFieldType.ARRAY).description("시작시각목록"),
                                    fieldWithPath("startList[].start").type(JsonFieldType.STRING).description("시작시각")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.scId").type(JsonFieldType.NUMBER).description("일정 id값"),
                                    fieldWithPath("data.scTitle").type(JsonFieldType.STRING).description("일정 제목"),
                                    fieldWithPath("data.scComment").type(JsonFieldType.STRING).description("일정 세부내용"),
                                    fieldWithPath("data.scStart").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.scEnd").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("스케줄 PATCH 요청이 들어올 때")
    class EditSchedulesTestCase {
        // given
        Long scId = 1L;
        String url = "/schedules/" + scId;
        ScheduleTime selTimeOne = new ScheduleTime(LocalDateTime.of(2023, 4, 10, 16, 0));
        ScheduleTime selTimeTwo = new ScheduleTime(LocalDateTime.of(2023, 4, 10, 18, 0));
        String scTitle = "수정한 일정 제목";
        String scComment = "수정한 일정 내용";
        ScheduleRequestDto request = new ScheduleRequestDto(scTitle, scComment, List.of(selTimeOne, selTimeTwo));

        @Test
        void 스케줄을_수정한다() throws Exception {
            // given
            ScheduleResponseDto response = ScheduleResponseDto.builder()
                    .scTitle(scTitle)
                    .scComment(scComment)
                    .scId(scId)
                    .scStart(selTimeOne.getStart())
                    .scEnd(selTimeTwo.getStart())
                    .build();

            when(scheduleService.editSchedule(eq(scId), any(ScheduleRequestDto.class), any(UserDetailsImpl.class)))
                    .thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("patch-schedule",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("scTitle").type(JsonFieldType.STRING).description("일정 제목"),
                                    fieldWithPath("scComment").type(JsonFieldType.STRING).description("일정 세부내용"),
                                    fieldWithPath("startList").type(JsonFieldType.ARRAY).description("시작시각목록"),
                                    fieldWithPath("startList[].start").type(JsonFieldType.STRING).description("시작시각")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.scId").type(JsonFieldType.NUMBER).description("일정 id값"),
                                    fieldWithPath("data.scTitle").type(JsonFieldType.STRING).description("일정 제목"),
                                    fieldWithPath("data.scComment").type(JsonFieldType.STRING).description("일정 세부내용"),
                                    fieldWithPath("data.scStart").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.scEnd").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("스케줄 DELETE 요청이 들어올 때")
    class DeleteSchedulesTestCase {
        // given
        Long scId = 1L;
        String url = "/schedules/" + scId;

        @Test
        void 스케줄을_삭제한다() throws Exception {
            // given

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("delete-schedule",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.NULL).description("결과값")
                            )
                    ));
        }
    }
}