package com.example.chillisauce.spaces.controller;




import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.service.BoxService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class BoxControllerTest {

    @InjectMocks
    private BoxController boxController;
    @Mock
    private BoxService boxService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(boxController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();

    }

    @Nested
    @DisplayName("Box 컨트롤러 성공 케이스")
    class ControllerSuccessCase {
        @Test
        @WithMockUser
        void Box_생성_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            String url = "/boxes/" + companyName + "/" + spaceId;

            BoxRequestDto boxRequestDto = new BoxRequestDto("Box 생성 테스트", "200", "300");
            BoxResponseDto boxResponseDto = new BoxResponseDto(1L, "Box 생성 테스트", null, "200", "300");
            when(boxService.createBox(eq(companyName), eq(spaceId), any(), any())).thenReturn(boxResponseDto);


            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boxRequestDto)));


            //then
            result.andExpect(status().isOk())
                    .andDo(document("post-createBox",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("boxName").type(JsonFieldType.STRING).description("박스 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("박스 X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("박스 Y 좌표")

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
        void Box_수정_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long boxId = 1L;
            String url = "/boxes/" + companyName + "/" + boxId;
            BoxRequestDto boxRequestDto = new BoxRequestDto("Box 수정 테스트", "200", "300");
            BoxResponseDto boxResponseDto = new BoxResponseDto(1L, "Box 수정 테스트", null, "200", "300");
            when(boxService.updateBox(eq(companyName), eq(boxId), any(), any())).thenReturn(boxResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boxRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-updateBox",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("boxName").type(JsonFieldType.STRING).description("박스 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("박스 X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("박스 Y 좌표")

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
        void Box_삭제_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long boxId = 1L;
            String url = "/boxes/" + companyName + "/" + boxId;
            BoxResponseDto boxResponseDto = new BoxResponseDto(1L, "Box 삭제 테스트", null, "200", "300");
            when(boxService.deleteBox(eq(companyName), eq(boxId), any())).thenReturn(boxResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("delete-deleteBox",
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
        void Box_사용자_등록_및_BOX_이동_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long formBoxId = 1L;
            Long toBoxId = 2L;
            String url = "/boxes/" + companyName + "/" + toBoxId + "/move" ;
            BoxRequestDto boxRequestDto = new BoxRequestDto("Box 수정 테스트", "200", "300");
            BoxResponseDto boxResponseDto = new BoxResponseDto(1L, "Box 수정 OK???", null, "200", "300");
            when(boxService.moveBoxWithUser(eq(companyName), eq(toBoxId), any(), any())).thenReturn(boxResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(boxRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-moveBoxWithUser",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("boxName").type(JsonFieldType.STRING).description("박스 이름"),
                                    fieldWithPath("x").type(JsonFieldType.STRING).description("박스 X 좌표"),
                                    fieldWithPath("y").type(JsonFieldType.STRING).description("박스 Y 좌표")

                            ),

                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data").type(JsonFieldType.STRING).description("")

                            )
                    ));
        }
    }
}



