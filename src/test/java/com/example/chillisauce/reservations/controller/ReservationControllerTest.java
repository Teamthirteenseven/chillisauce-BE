package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.reservations.dto.*;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation){
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("예약 컨트롤러 성공 케이스")
    class ControllerSuccessCase {
        @Test
        @WithMockUser
        void 예약조회성공_회사_전체() throws Exception {
            // given
            String companyName = "testCompany";
            String url = "/reservations/" +companyName+"/all";
            ReservationListResponseDto all = getAllReservationResponse();
            when(reservationService.getAllReservations(eq(companyName), any())).thenReturn(all);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                            .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("get-all-reservations",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.reservationList").type(JsonFieldType.ARRAY).description("예약리스트"),
                                    fieldWithPath("data.reservationList[].reservationId").type(JsonFieldType.NUMBER).description("예약 id"),
                                    fieldWithPath("data.reservationList[].mrId").type(JsonFieldType.NUMBER).description("회의실 id"),
                                    fieldWithPath("data.reservationList[].username").type(JsonFieldType.STRING).description("예약자 이름"),
                                    fieldWithPath("data.reservationList[].start").type(JsonFieldType.STRING).description("예약 시작 시각"),
                                    fieldWithPath("data.reservationList[].end").type(JsonFieldType.STRING).description("예약 종료 시각")
                            )
                    ));

        }

        private ReservationListResponseDto getAllReservationResponse() {
            List<ReservationDetailResponseDto> responseList = new ArrayList<>();
            ReservationDetailResponseDto reservationOne = ReservationDetailResponseDto.builder()
                    .reservationId(1L)
                    .username("강백호")
                    .mrId(1L)
                    .start(LocalDateTime.of(2023, 4, 11, 15, 0))
                    .end(LocalDateTime.of(2023, 4, 11, 15, 59))
                    .build();

            ReservationDetailResponseDto reservationTwo = ReservationDetailResponseDto.builder()
                    .reservationId(2L)
                    .username("윤대협")
                    .mrId(3L)
                    .start(LocalDateTime.of(2023, 4, 12, 17, 0))
                    .end(LocalDateTime.of(2023, 4, 12, 17, 59))
                    .build();

            responseList.add(reservationOne);
            responseList.add(reservationTwo);
            return new ReservationListResponseDto(responseList);
        }

        @Test
        @WithMockUser
        void 예약조회성공_특정날짜_특정회의실 () throws Exception{
            // given
            String url = "/reservations/1";
            ReservationTimetableResponseDto timeTable = getReservationTimeTable();
            when(reservationService.getReservationTimetable(any(), anyLong(), any())).thenReturn(timeTable);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .param("selDate", "2023-04-13")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("get-reservation-timetable",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.mrId").type(JsonFieldType.NUMBER).description("회의실 id"),
                                    fieldWithPath("data.timeList").type(JsonFieldType.ARRAY).description("타임테이블"),
                                    fieldWithPath("data.timeList[].isCheckOut").type(JsonFieldType.BOOLEAN).description("예약 여부"),
                                    fieldWithPath("data.timeList[].start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.timeList[].end").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }

        private ReservationTimetableResponseDto getReservationTimeTable() {
            Long mrId = 1L;

            ReservationTimeResponseDto timeOne = ReservationTimeResponseDto.builder().isCheckOut(false)
                    .start(LocalTime.of(7, 0))
                    .end(LocalTime.of(7, 59))
                    .build();

            ReservationTimeResponseDto timeTwo = ReservationTimeResponseDto.builder().isCheckOut(true)
                    .start(LocalTime.of(8, 0))
                    .end(LocalTime.of(8, 59))
                    .build();

            ReservationTimeResponseDto timeLast = ReservationTimeResponseDto.builder()
                    .isCheckOut(false)
                    .start(LocalTime.of(22, 0))
                    .end(LocalTime.of(22, 59))
                    .build();

            return new ReservationTimetableResponseDto(mrId, List.of(timeOne, timeTwo, timeLast));
        }

        @Test
        @WithMockUser
        void 예약등록성공() throws Exception {
            // given
            String url = "/reservations/1";
            LocalDateTime start = LocalDateTime.of(2023, 4, 10, 12, 0);
            LocalDateTime end = LocalDateTime.of(2023, 4, 10, 12, 59);
            ReservationResponseDto response = new ReservationResponseDto(start, end);
            when(reservationService.addReservation(eq(1L), any(), any(UserDetailsImpl.class))).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"start\":\"2023-04-10T12:00\", " +
                            "\"end\":\"2023-04-10T12:59\"}"));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("post-reservation",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("end").type(JsonFieldType.STRING).description("종료시각")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.end").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }

        @Test
        @WithMockUser
        void 예약수정성공() throws Exception{
            // given
            String url = "/reservations/1";
            LocalDateTime start = LocalDateTime.of(2023, 4, 10, 12, 0);
            LocalDateTime end = LocalDateTime.of(2023, 4, 10, 12, 59);
            ReservationResponseDto response = new ReservationResponseDto(start, end);
            when(reservationService.editReservation(eq(1L), any(), any())).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"start\":\"2023-04-10T12:00\", \"end\":\"2023-04-10T12:59\"}"));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("patch-reservation",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("end").type(JsonFieldType.STRING).description("종료시각")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.end").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }

        @Test
        @WithMockUser
        void 예약삭제성공() throws Exception{
            // given
            String url = "/reservations/1";
            when(reservationService.deleteReservation(eq(1L), any())).thenReturn("");

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("delete-reservation",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("결과값")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 컨트롤러 실패 케이스")
    class ControllerFailCase {

    }
}