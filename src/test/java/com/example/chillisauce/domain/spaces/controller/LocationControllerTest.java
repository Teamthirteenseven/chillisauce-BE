package com.example.chillisauce.domain.spaces.controller;

import com.example.chillisauce.domain.spaces.controller.LocationController;
import com.example.chillisauce.domain.spaces.dto.request.BoxRequestDto;
import com.example.chillisauce.domain.spaces.dto.response.LocationDto;
import com.example.chillisauce.domain.spaces.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class LocationControllerTest {
    @InjectMocks
    private LocationController locationController;
    @Mock
    private LocationService locationService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(locationController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();

    }

    @Test
    @WithMockUser
    void 사용자_등록_및_BOX_MultiBox_이동_성공() throws Exception {
        //given
        String companyName = "test";
        Long locationId = 1L;
        String url = "/locations/" + companyName +"/" + locationId;
        BoxRequestDto boxRequestDto = new BoxRequestDto("테스트", "200", "300");
        LocationDto locationDto = new LocationDto(1L, "테스트",  "200", "300");
        when(locationService.moveWithUser(eq(companyName),eq(locationId), any())).thenReturn(locationDto);

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .header("Authorization", "Bearer Token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boxRequestDto)));

        //then
        result.andExpect(status().isOk())
                .andDo(document("patch-moveWithUser",
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


