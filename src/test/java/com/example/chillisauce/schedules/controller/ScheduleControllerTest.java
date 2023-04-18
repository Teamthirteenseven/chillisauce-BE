package com.example.chillisauce.schedules.controller;

import com.example.chillisauce.reservations.controller.ReservationController;
import com.example.chillisauce.reservations.dto.ReservationDetailResponseDto;
import com.example.chillisauce.reservations.dto.ReservationListResponseDto;
import com.example.chillisauce.reservations.exception.ReservationExceptionHandler;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.schedules.dto.ScheduleListResponseDto;
import com.example.chillisauce.schedules.dto.ScheduleResponseDto;
import com.example.chillisauce.schedules.service.ScheduleService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@DisplayName("ReservationController 클래스")
class ScheduleControllerTest {
    @InjectMocks
    private ScheduleController scheduleController;

    @Mock
    private ScheduleService scheduleService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(scheduleController)
                .setControllerAdvice(new ReservationExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("전체 스케줄 GET 요청이 들어오면")
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
                    .scEnd(LocalDateTime.of(2023, 4, 18, 18,0 ))
                    .build();

            responseList.add(scheduleOne);
            responseList.add(scheduleTwo);
            return new ScheduleListResponseDto(responseList);
        }
    }

    @Nested
    @DisplayName("당일 스케줄 타임테이블 GET 요청이 들어오면")
    class GetDaySchedulesTestCase {

    }

    @Nested
    @DisplayName("스케줄 POST 요청이 들어오면")
    class AddSchedulesTestCase {

    }

    @Nested
    @DisplayName("스케줄 PATCH 요청이 들어오면")
    class EditSchedulesTestCase {

    }

    @Nested
    @DisplayName("스케줄 DELETE 요청이 들어오면")
    class DeleteSchedulesTestCase {

    }
}