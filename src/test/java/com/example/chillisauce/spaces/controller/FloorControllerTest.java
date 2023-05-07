package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.spaces.dto.request.FloorRequestDto;
import com.example.chillisauce.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.spaces.service.FloorService;
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
public class FloorControllerTest {

    @InjectMocks
    private FloorController floorController;
    @Mock
    private FloorService floorService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(floorController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();

    }

    @Nested
    @DisplayName("Floor 컨트롤러 성공 케이스")
    class ControllerSuccessCase {
        @Test
        @WithMockUser
        void Floor_생성_성공() throws Exception {
            //given
            String companyName = "testCompany";
            String url = "/floors/" + companyName;

            FloorRequestDto floorRequestDto = new FloorRequestDto("Floor 생성 테스트");
            FloorResponseDto floorResponseDto = new FloorResponseDto(1L, "Floor 생성 테스트", new ArrayList<>());
            when(floorService.createFloor(eq(companyName), any(), any())).thenReturn(floorResponseDto);


            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(floorRequestDto)));


            //then
            result.andExpect(status().isOk())
                    .andDo(document("post-createFloor",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("floorName").type(JsonFieldType.STRING).description("Floor 이름")
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
        void Floor_선택조회_성공() throws Exception{
            //given
            String companyName = "testCompany";
            Long floorId = 1L;
            String url = "/floors/" + companyName + "/" + floorId;

            List<FloorResponseDto> responseDtoList = new ArrayList<>();
            responseDtoList.add(new FloorResponseDto(1L, "Floor 생성 테스트",new ArrayList<>()));

            FloorRequestDto floorRequestDto = new FloorRequestDto("Floor 생성 테스트");
            when(floorService.getFloorlist(eq(companyName), any(), any())).thenReturn(responseDtoList);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(floorRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-getFloorlist",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("floorName").type(JsonFieldType.STRING).description("Floor 이름")
                            ),

                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과값"),
                                    fieldWithPath("data[].floorId").type(JsonFieldType.NUMBER).description("Floor id"),
                                    fieldWithPath("data[].floorName").type(JsonFieldType.STRING).description("Floor 이름"),
                                    fieldWithPath("data[].spaceList[]").type(JsonFieldType.ARRAY).description("SpaceList")
                            )
                    ));
        }
        @Test
        @WithMockUser
        void Floor만_조회_성공() throws Exception {
            //given
            String companyName = "testCompany";
            String url = "/floors/" + companyName;

            List<FloorResponseDto> responseDtoList = new ArrayList<>();
            responseDtoList.add(new FloorResponseDto(1L, "Floor 수정 테스트",new ArrayList<>()));

            FloorRequestDto floorRequestDto = new FloorRequestDto("Floor 수정 테스트");
            when(floorService.getFloor(eq(companyName), any())).thenReturn(responseDtoList);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(floorRequestDto)));
            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-getFloor",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("floorName").type(JsonFieldType.STRING).description("Floor 이름")
                            ),

                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("결과값"),
                                    fieldWithPath("data[].floorId").type(JsonFieldType.NUMBER).description("Floor id"),
                                    fieldWithPath("data[].floorName").type(JsonFieldType.STRING).description("Floor 이름"),
                                    fieldWithPath("data[].spaceList[]").type(JsonFieldType.ARRAY).description("SpaceList")
                            )
                    ));
        }
        @Test
        @WithMockUser
        void Floor_수정_성공() throws Exception {
            //given
            String companyName = "testCompany";
            Long floorId = 1L;
            String url = "/floors/" + companyName + "/" + floorId;
            FloorRequestDto floorRequestDto = new FloorRequestDto("Floor 생성 테스트");
            FloorResponseDto floorResponseDto = new FloorResponseDto(1L, "Floor 생성 테스트",new ArrayList<>());
            when(floorService.updateFloor(eq(companyName), eq(floorId), any(), any())).thenReturn(floorResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(floorRequestDto)));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-updateFloor",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("floorName").type(JsonFieldType.STRING).description("Floor 이름")

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
            Long floorId = 1L;
            String url = "/floors/" + companyName + "/" + floorId;
            FloorResponseDto floorResponseDto = new FloorResponseDto(1L, "Floor 생성 테스트",new ArrayList<>());
            when(floorService.deleteFloor(eq(companyName), eq(floorId), any())).thenReturn(floorResponseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("delete-deleteFloor",
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
