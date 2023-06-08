package com.example.chillisauce.domain.spaces.controller;

import com.example.chillisauce.domain.reservations.dto.response.ReservationResponse;
import com.example.chillisauce.domain.spaces.controller.MrController;
import com.example.chillisauce.domain.spaces.dto.request.MrRequestDto;
import com.example.chillisauce.domain.spaces.dto.response.MrResponseDto;
import com.example.chillisauce.domain.spaces.service.MrService;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class MrControllerTest {

    @InjectMocks
    private MrController mrController;

    @Mock
    private MrService mrService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(mrController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    @Nested
    @DisplayName("Mr 컨트롤러 성공 케이스")
    class ControllerSuccessCase {
        @Test
        @WithMockUser
        void Mr_생성_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            String url = "/mr/" + companyName + "/" + spaceId;

            MrRequestDto mrRequestDto = new MrRequestDto("Mr 테스트 생성", "777", "888");
            MrResponseDto mrResponseDto = new MrResponseDto(1L, "Mr 테스트 생성", "777", "888");
            when(mrService.createMr(eq(companyName), eq(spaceId), any(), any())).thenReturn(mrResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mrRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("post-createMr",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("mrName").type(JsonFieldType.STRING).description("Mr 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("Mr X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("Mr Y 좌표")

                            ),

                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("")

                            )
                    ));
        }
        @Test
        @WithMockUser
        void Mr_수정_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long mrId = 1L;
            String url = "/mr/" + companyName + "/" + mrId;
            MrRequestDto mrRequestDto = new MrRequestDto("Mr 수정 OK?", "777", "888");
            MrResponseDto mrResponseDto = new MrResponseDto(1L, "Mr 수정 OK?", "777", "888");
            when(mrService.updateMr(eq(companyName), eq(mrId), any(), any())).thenReturn(mrResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mrRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-updateMr",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("mrName").type(JsonFieldType.STRING).description("Mr 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("Mr X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("Mr Y 좌표")

                            ),

                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("")

                            )
                    ));
        }

        @Test
        @WithMockUser
        void Mr_삭제_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long mrId = 1L;
            String url = "/mr/" + companyName + "/" + mrId;
            MrResponseDto mrResponseDto = new MrResponseDto(1L, "Mr 테스트 생성", "777", "888");
            when(mrService.deleteMr(eq(companyName), eq(mrId), any())).thenReturn(mrResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("delete-deleteMr",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("")

                            )
                    ));
        }

        @Test
        @WithMockUser
        void Mr_조회_성공() throws  Exception {
            //given
            String companyName = "testCompany";
            String url = "/mr/" + companyName;
            List<MrResponseDto> mrResponseDtoList = new ArrayList<>();
            List<ReservationResponse> reservationList = new ArrayList<>();
            mrResponseDtoList.add(new MrResponseDto(1L,"Mr 테스트 생성", "777" , "888", reservationList));

            when(mrService.mrlist(eq(companyName), any())).thenReturn(mrResponseDtoList);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-mrlist",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과값"),
                                    fieldWithPath("data[].mrId").type(JsonFieldType.NUMBER).description("mr id"),
                                    fieldWithPath("data[].mrName").type(JsonFieldType.STRING).description("mr 이름"),
                                    fieldWithPath("data[].x").type(JsonFieldType.STRING).description("Mr X값 좌표"),
                                    fieldWithPath("data[].y").type(JsonFieldType.STRING).description("Mr Y값 좌표"),
                                    fieldWithPath("data[].reservationList[]").type(JsonFieldType.ARRAY).description("예약 리스트")
                            )
                    ));
        }
    }
}
