package com.example.chillisauce.domain.reservations.controller;

import com.example.chillisauce.domain.reservations.controller.UserReservationController;
import com.example.chillisauce.domain.reservations.dto.response.UserReservationListResponse;
import com.example.chillisauce.domain.reservations.dto.response.UserReservationResponse;
import com.example.chillisauce.domain.reservations.dto.response.UsernameResponse;
import com.example.chillisauce.domain.reservations.exception.ReservationExceptionHandler;
import com.example.chillisauce.domain.reservations.service.UserReservationService;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@DisplayName("UserReservationController 클래스")
class UserReservationControllerTest {
    @InjectMocks
    private UserReservationController userReservationController;

    @Mock
    private UserReservationService userReservationService;
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userReservationController)
                .setControllerAdvice(new ReservationExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("회원의 회의실 예약 내역 GET 요청 시")
    class GetUserReservationsTestCase {
        // given
        String url = "/users/reservations";

        UserReservationResponse reservationOne = UserReservationResponse.builder()
                .mrId(1L)
                .mrName("회의실1")
                .reservationId(1L)
                .username("김철수")
                .userList(List.of(new UsernameResponse("홍길동"), new UsernameResponse("임꺽정")))
                .start(LocalDateTime.of(2023, 4, 28, 15, 0))
                .end(LocalDateTime.of(2023, 4, 28, 15, 59))
                .build();

        UserReservationResponse reservationTwo = UserReservationResponse.builder()
                .mrId(2L)
                .mrName("회의실2")
                .reservationId(2L)
                .username("김철수")
                .userList(List.of(new UsernameResponse("채소연"), new UsernameResponse("성춘향")))
                .start(LocalDateTime.of(2023, 4, 29, 16, 0))
                .end(LocalDateTime.of(2023, 4, 29, 16, 59))
                .build();

        @Test
        @WithMockUser
        void 회원의_회의실_예약내역을_반환한다() throws Exception {
            // given
            UserReservationListResponse response = new UserReservationListResponse(List.of(reservationOne, reservationTwo));
            when(userReservationService.getUserReservations(any())).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("get-user-reservations",
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
                                    fieldWithPath("data.reservationList[].end").type(JsonFieldType.STRING).description("예약 종료 시각"),
                                    fieldWithPath("data.reservationList[].userList").type(JsonFieldType.ARRAY).description("참석자 목록"),
                                    fieldWithPath("data.reservationList[].userList[].username").type(JsonFieldType.STRING).description("참석자 이름")
                            )
                    ));
        }
    }
}