package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.reservations.dto.ReservationDetailResponseDto;
import com.example.chillisauce.reservations.dto.ReservationListResponseDto;
import com.example.chillisauce.reservations.service.ReservationService;
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
        void 회사_전체_예약조회_성공() throws Exception{
            // given
            String companyName = "testCompany";
            String url = "/"+companyName+"/reservations/all";
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
        void 예약등록_성공() throws Exception {
            // given
            String url = "/reservations/1";

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("{\"start\":\"2023-04-10T12:00\", \"end\":\"2023-04-10T14:00\"}"));

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
                                    fieldWithPath("data").type(JsonFieldType.NULL).description("결과값")
                            )
                    ));
        }

        @Test
        @WithMockUser
        void 예약수정성공() {

        }
    }

    @Nested
    @DisplayName("예약 컨트롤러 실패 케이스")
    class ControllerFailCase {

    }
}