package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.spaces.dto.FloorRequestDto;
import com.example.chillisauce.spaces.dto.FloorResponseDto;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.service.FloorService;
import com.example.chillisauce.spaces.service.SpaceService;
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
public class SpaceControllerTest {

    @InjectMocks
    private SpaceController spaceController;
    @Mock
    private SpaceService spaceService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(spaceController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();

    }

    @Nested
    @DisplayName("Space 컨트롤러 성공 케이스")
    class ControllerSuccessCase {
        @Test
        @WithMockUser
        void Space_생성_성공() throws Exception {
            //given
            String companyName = "testCompany";
            String url = "/spaces/" + companyName;

            SpaceRequestDto spaceRequestDto = new SpaceRequestDto("Test 공간");
            SpaceResponseDto spaceResponseDto = new SpaceResponseDto(1L, "Test 공간");
            when(spaceService.createSpace(eq(companyName), any(), any())).thenReturn(spaceResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(spaceRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("post-createSpace",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("spaceName").type(JsonFieldType.STRING).description("Space 이름")
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
        void Floor_안에_Space_생성_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long floorId = 1L;
            String url = "/spaces/" + companyName + "/" + floorId;

            SpaceRequestDto spaceRequestDto = new SpaceRequestDto("Test 공간");
            SpaceResponseDto spaceResponseDto = new SpaceResponseDto(1L, "Test 공간");
            when(spaceService.createSpaceinfloor(eq(companyName), any(), any(), eq(floorId))).thenReturn(spaceResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(spaceRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("post-createSpaceinfloor",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("spaceName").type(JsonFieldType.STRING).description("Space 이름")
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
        void Space_전체_조회() throws Exception {
            //given
            String companyName = "testCompany";
            String url = "/spaces/" + companyName;


            List<SpaceResponseDto> responseDtoList = new ArrayList<>();
            responseDtoList.add(new SpaceResponseDto(1L, "Test 공간"));
            when(spaceService.allSpacelist(eq(companyName), any())).thenReturn(responseDtoList);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-allSpacelist",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과값"),
                                    fieldWithPath("data[].spaceId").type(JsonFieldType.NUMBER).description("Space id"),
                                    fieldWithPath("data[].spaceName").type(JsonFieldType.STRING).description("Space 이름"),
                                    fieldWithPath("data[].floorId").type(JsonFieldType.NULL).description("Floor id"),
                                    fieldWithPath("data[].floorName").type(JsonFieldType.NULL).description("Floor name"),
                                    fieldWithPath("data[].boxlist[]").type(JsonFieldType.ARRAY).description("BoxList"),
                                    fieldWithPath("data[].mrlist[]").type(JsonFieldType.ARRAY).description("MrList"),
                                    fieldWithPath("data[].multiboxlist[]").type(JsonFieldType.ARRAY).description("MultiBoxList")
                            )
                    ));

        }

        @Test
        @WithMockUser
        void Space_선택_조회() throws Exception {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            String url = "/spaces/" + companyName + "/" + spaceId;

            List<SpaceResponseDto> responseDtoList = new ArrayList<>();
            responseDtoList.add(new SpaceResponseDto(1L, "Test 공간"));
            when(spaceService.getSpacelist(eq(companyName), eq(spaceId), any())).thenReturn(responseDtoList);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-getSpacelist",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과값"),
                                    fieldWithPath("data[].spaceId").type(JsonFieldType.NUMBER).description("Space id"),
                                    fieldWithPath("data[].spaceName").type(JsonFieldType.STRING).description("Space 이름"),
                                    fieldWithPath("data[].floorId").type(JsonFieldType.NULL).description("Floor id"),
                                    fieldWithPath("data[].floorName").type(JsonFieldType.NULL).description("Floor name"),
                                    fieldWithPath("data[].boxlist[]").type(JsonFieldType.ARRAY).description("BoxList"),
                                    fieldWithPath("data[].mrlist[]").type(JsonFieldType.ARRAY).description("MrList"),
                                    fieldWithPath("data[].multiboxlist[]").type(JsonFieldType.ARRAY).description("MultiBoxList")
                            )
                    ));
        }

        @Test
        @WithMockUser
        void Space_수정_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            String url = "/spaces/" + companyName + "/" + spaceId;
            SpaceRequestDto spaceRequestDto = new SpaceRequestDto("수정 성공했나요?");
            SpaceResponseDto spaceResponseDto = new SpaceResponseDto(1L, "수정 성공했나요?");
            when(spaceService.updateSpace(eq(companyName), eq(spaceId), any(), any())).thenReturn(spaceResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(spaceRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-updateSpace",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("spaceName").type(JsonFieldType.STRING).description("Space 이름")
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
        void Floor_삭제_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            String url = "/spaces/" + companyName + "/" + spaceId;
            SpaceResponseDto spaceResponseDto = new SpaceResponseDto(1L, "Test 공간");
            when(spaceService.deleteSpace(eq(companyName), eq(spaceId), any())).thenReturn(spaceResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("delete-deleteSpace",
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

