package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.spaces.dto.request.MultiBoxRequestDto;
import com.example.chillisauce.spaces.dto.response.MultiBoxResponseDto;
import com.example.chillisauce.spaces.service.MultiBoxService;
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

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class MultiBoxControllerTest {

    @InjectMocks
    private MultiBoxController multiBoxController;
    @Mock
    private MultiBoxService multiBoxService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(multiBoxController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();

    }

    @Nested
    @DisplayName("Box 컨트롤러 성공 케이스")
    class ControllerSuccessCase {
        @Test
        @WithMockUser
        void MultiBox_생성_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            String url = "/multiBox/" + companyName + "/" + spaceId;

            MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBox 생성 테스트", "555", "444");
            MultiBoxResponseDto multiBoxResponseDto = new MultiBoxResponseDto(1L, "MultiBox 생성 테스트", "200", "300", null);
            when(multiBoxService.createMultiBox(eq(companyName), eq(spaceId), any(), any())).thenReturn(multiBoxResponseDto);


            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(multiBoxRequestDto)));


            //then
            result.andExpect(status().isOk())
                    .andDo(document("post-createMultiBox",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("multiBoxName").type(JsonFieldType.STRING).description("MultiBox 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("MultiBox X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("MultiBox Y 좌표")

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
        void MultiBox_수정_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long multiboxId = 1L;
            String url = "/multiBox/" + companyName + "/" + multiboxId;
            MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBox 수정 OK???", "555", "444");
            MultiBoxResponseDto multiBoxResponseDto = new MultiBoxResponseDto(1L, "MultiBox 수정 OK???", "200", "300", null);
            when(multiBoxService.updateMultiBox(eq(companyName), eq(multiboxId), any(), any())).thenReturn(multiBoxResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(multiBoxRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-updateMultiBox",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("multiBoxName").type(JsonFieldType.STRING).description("MultiBox 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("MultiBox X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("MultiBox Y 좌표")

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
        void MultiBox_삭제_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long multiboxId = 1L;
            String url = "/multiBox/" + companyName + "/" + multiboxId;
            MultiBoxResponseDto multiBoxResponseDto = new MultiBoxResponseDto(1L, "MultiBox 생성 테스트", "200", "300", null);
            when(multiBoxService.deleteMultiBox(eq(companyName), eq(multiboxId), any())).thenReturn(multiBoxResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("delete-deleteMultiBox",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("")

                            )
                    ));
        }
    }
}
