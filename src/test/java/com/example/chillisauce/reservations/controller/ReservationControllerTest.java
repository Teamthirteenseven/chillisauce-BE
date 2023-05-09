package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.dto.request.ReservationRequest;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.dto.request.ReservationAttendee;
import com.example.chillisauce.reservations.dto.response.*;
import com.example.chillisauce.reservations.exception.ReservationExceptionHandler;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
@DisplayName("ReservationController 클래스")
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .setControllerAdvice(new ReservationExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("회사 전체 회의실 예약 내역 GET 요청 시")
    class GetAllReservationTestCase {
        // given
        String companyName = "testCompany";
        String url = "/reservations/" + companyName + "/all";
        Integer page = 1;

        @Test
        @WithMockUser
        void 전체_회의실_예약내역을_반환한다() throws Exception {
            // given
            ReservationListResponse all = getAllReservationResponse();
            when(reservationService.getAllReservations(eq(companyName), eq(page - 1), any())).thenReturn(all);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .param("page", "1")
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
                                    fieldWithPath("data.reservationList[].mrName").type(JsonFieldType.STRING).description("회의실 이름"),
                                    fieldWithPath("data.reservationList[].username").type(JsonFieldType.STRING).description("예약자 이름"),
                                    fieldWithPath("data.reservationList[].start").type(JsonFieldType.STRING).description("예약 시작 시각"),
                                    fieldWithPath("data.reservationList[].end").type(JsonFieldType.STRING).description("예약 종료 시각")
                            )
                    ));
        }

        private ReservationListResponse getAllReservationResponse() {
            List<ReservationDetailResponse> responseList = new ArrayList<>();
            ReservationDetailResponse reservationOne = ReservationDetailResponse.builder()
                    .reservationId(1L)
                    .username("강백호")
                    .mrId(1L)
                    .mrName("회의실1")
                    .start(LocalDateTime.of(2023, 4, 11, 15, 0))
                    .end(LocalDateTime.of(2023, 4, 11, 15, 59))
                    .build();

            ReservationDetailResponse reservationTwo = ReservationDetailResponse.builder()
                    .reservationId(2L)
                    .username("윤대협")
                    .mrId(3L)
                    .mrName("회의실2")
                    .start(LocalDateTime.of(2023, 4, 12, 17, 0))
                    .end(LocalDateTime.of(2023, 4, 12, 17, 59))
                    .build();

            responseList.add(reservationOne);
            responseList.add(reservationTwo);
            return new ReservationListResponse(responseList);
        }

        @Test
        @WithAnonymousUser
        void 인증된_회원이_아니면_인증에러코드를_반환한다() {
            String companyName = "testCompany";
            String url = "/reservations/" + companyName + "/all";
        }
    }

    @Nested
    @DisplayName("예약 타임테이블 GET 요청 시")
    class GetReservationTimeTableTestCase {
        // given
        String url = "/reservations/1";
        ReservationTimetableResponse timeTable = getReservationTimetable();

        @Test
        @WithMockUser
        void 특정날짜_특정회의실의_예약테이블을_반환한다() throws Exception {
            // given
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
                                    fieldWithPath("data.mrName").type(JsonFieldType.STRING).description("회의실 이름"),
                                    fieldWithPath("data.timeList").type(JsonFieldType.ARRAY).description("타임테이블"),
                                    fieldWithPath("data.timeList[].isCheckOut").type(JsonFieldType.BOOLEAN).description("예약 여부"),
                                    fieldWithPath("data.timeList[].start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.timeList[].end").type(JsonFieldType.STRING).description("종료시각")
                            )
                    ));
        }

        private ReservationTimetableResponse getReservationTimetable() {
            Long mrId = 1L;
            String mrName = "testMeetingRoom";
            ReservationTimeResponse timeOne = ReservationTimeResponse.builder().isCheckOut(false)
                    .start(LocalTime.of(7, 0))
                    .end(LocalTime.of(7, 59))
                    .build();

            ReservationTimeResponse timeTwo = ReservationTimeResponse.builder().isCheckOut(true)
                    .start(LocalTime.of(8, 0))
                    .end(LocalTime.of(8, 59))
                    .build();

            ReservationTimeResponse timeLast = ReservationTimeResponse.builder()
                    .isCheckOut(false)
                    .start(LocalTime.of(22, 0))
                    .end(LocalTime.of(22, 59))
                    .build();

            return new ReservationTimetableResponse(mrId, mrName, List.of(timeOne, timeTwo, timeLast));
        }
    }

    @Nested
    @DisplayName("예약 POST 요청 시")
    class AddReservationTestCase {
        // given
        String url = "/reservations/1";
        LocalDateTime startOne = LocalDateTime.of(2023, 4, 10, 13, 0);
        LocalDateTime startTwo = LocalDateTime.of(2023, 4, 10, 14, 0);
        LocalDateTime end = LocalDateTime.of(2023, 4, 10, 14, 59);
        ReservationTime requestOne = new ReservationTime(startOne);
        ReservationTime requestTwo = new ReservationTime(startTwo);
        ReservationAttendee userOne = new ReservationAttendee(1L);
        ReservationAttendee userTwo = new ReservationAttendee(2L);
        List<ReservationAttendee> userList = List.of(userOne, userTwo);
        ReservationRequest requestBody = new ReservationRequest(List.of(requestOne, requestTwo), userList);
        ReservationResponse response = new ReservationResponse(startOne, end, new ArrayList<>());

        @Test
        @WithMockUser
        void 예약을_등록한다() throws Exception {
            when(reservationService.addReservation(eq(1L), any(), any(UserDetailsImpl.class))).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("post-reservation",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("startList").type(JsonFieldType.ARRAY).description("시작시각목록"),
                                    fieldWithPath("startList[].start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("userList").type(JsonFieldType.ARRAY).description("참석자 목록"),
                                    fieldWithPath("userList[].userId").type(JsonFieldType.NUMBER).description("참석자 id값")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.end").type(JsonFieldType.STRING).description("종료시각"),
                                    fieldWithPath("data.userList").type(JsonFieldType.ARRAY).description("참석자 이")
                            )
                    ));
        }

        @Test
        @WithMockUser
        void 예약_시간리스트가_비어있으면_예외를_반환한다() throws Exception {
            // given
            String url = "/reservations/1";
            String message = "요청의 시각 목록이 비어있습니다.";
            ResponseEntity<ResponseMessage<Object>> response = ResponseMessage.responseError(message, HttpStatus.BAD_REQUEST);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"startList\":[]}"));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(document("post-reservation-empty-list",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("startList").type(JsonFieldType.ARRAY).description("시작시각목록")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("에러 데이터")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 PATCH 요청 시")
    class EditReservationTestCase {
        // given
        String url = "/reservations/1";
        LocalDateTime startOne = LocalDateTime.of(2023, 4, 10, 13, 0);
        LocalDateTime startTwo = LocalDateTime.of(2023, 4, 10, 14, 0);
        LocalDateTime end = LocalDateTime.of(2023, 4, 10, 14, 59);
        ReservationTime requestOne = new ReservationTime(startOne);
        ReservationTime requestTwo = new ReservationTime(startTwo);
        ReservationAttendee userOne = new ReservationAttendee(1L);
        ReservationAttendee userTwo = new ReservationAttendee(2L);
        List<ReservationAttendee> userList = List.of(userOne, userTwo);
        ReservationRequest requestBody = new ReservationRequest(List.of(requestOne, requestTwo), userList);
        ReservationResponse response = new ReservationResponse(startOne, end, new ArrayList<>());

        @Test
        @WithMockUser
        void 예약을_수정한다() throws Exception {
            when(reservationService.editReservation(eq(1L), any(), any())).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("patch-reservation",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("startList").type(JsonFieldType.ARRAY).description("시작시각목록"),
                                    fieldWithPath("startList[].start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("userList").type(JsonFieldType.ARRAY).description("참석자 목록"),
                                    fieldWithPath("userList[].userId").type(JsonFieldType.NUMBER).description("참석자 id값")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.start").type(JsonFieldType.STRING).description("시작시각"),
                                    fieldWithPath("data.end").type(JsonFieldType.STRING).description("종료시각"),
                                    fieldWithPath("data.userList").type(JsonFieldType.ARRAY).description("참석자 이름")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 DELETE 요청 시")
    class DeleteReservationTestCase {
        @Test
        @WithMockUser
        void 예약을_삭제한다() throws Exception {
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
}