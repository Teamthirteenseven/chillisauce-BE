package com.example.chillisauce.domain.users.controller;

import com.example.chillisauce.domain.users.controller.AdminController;
import com.example.chillisauce.domain.users.dto.request.RoleDeptUpdateRequestDto;
import com.example.chillisauce.domain.users.dto.response.UserDetailResponseDto;
import com.example.chillisauce.domain.users.dto.response.UserListResponseDto;
import com.example.chillisauce.domain.users.entity.UserRoleEnum;
import com.example.chillisauce.domain.users.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class AdminControllerTest {
    @InjectMocks
    private AdminController adminController;
    @Mock
    AdminService adminService;
    private MockMvc mockMvc;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminController)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    UserDetailResponseDto userOne = UserDetailResponseDto.builder()
            .userId(1L)
            .email("1234@qwer")
            .username("펩 과르디올라")
            .role(UserRoleEnum.ADMIN)
            .build();
    UserDetailResponseDto userTwo = UserDetailResponseDto.builder()
            .userId(1L)
            .email("1234@1234")
            .username("우주 최고 미남 홀란")
            .role(UserRoleEnum.USER)
            .build();

    private UserListResponseDto getUserList() {
        List<UserDetailResponseDto> response = new ArrayList<>();
        response.add(userOne);
        response.add(userTwo);
        return new UserListResponseDto(response);
    }

    @Nested
    @DisplayName("어드민 컨트롤러 성공 케이스")
    class ControllerSuccessCase {

        @DisplayName("사원 목록 조회")
        @Test
        void success1() throws Exception {
            //given
            UserListResponseDto responseDto = getUserList();
            Mockito.when(adminService.getAllUsers(any())).thenReturn(responseDto);
            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                    .get("/admin/users")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-all-userList",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.userList").type(JsonFieldType.ARRAY).description("유저리스트"),
                                    fieldWithPath("data.userList[].userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                    fieldWithPath("data.userList[].email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("data.userList[].username").type(JsonFieldType.STRING).description("유저 이름"),
                                    fieldWithPath("data.userList[].role").type(JsonFieldType.STRING).description("유저 권한")
                            )
                    ));
        }


        @DisplayName("사원 선택 조회")
        @Test
        void success2() throws Exception {
            //given
            UserDetailResponseDto responseDto = userTwo;
            when(adminService.getUsers(any(), any())).thenReturn(responseDto);

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                    .get("/admin/users/2")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andDo(document("get-user",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                    fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 이름"),
                                    fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 권한")
                            )
                    ));

        }

        @DisplayName("사원 권한 수정")
        @Test
        void success3() throws Exception {
            //given
            UserDetailResponseDto edit = UserDetailResponseDto.builder()
                    .userId(2L)
                    .email("1234@1234")
                    .username("우주 최고 미남 홀란")
                    .role(UserRoleEnum.MANAGER)
                    .build();
            RoleDeptUpdateRequestDto requestDto = RoleDeptUpdateRequestDto.builder()
                    .role(UserRoleEnum.MANAGER)
                    .updateRole(true)
                    .build();
            when(adminService.editUser(eq(2L), any(), any())).thenReturn(edit);
            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                    .patch("/admin/users/2")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)));
            //then
            result.andExpect(status().isOk())
                    .andDo(document("patch-user-role",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("role").type(JsonFieldType.STRING).description("유저 권한"),
                                    fieldWithPath("updateRole").type(JsonFieldType.BOOLEAN).description("수정 여부")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과값"),
                                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                    fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 이름"),
                                    fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 권한")

                            )
                    ));

        }

        @DisplayName("사원 삭제")
        @Test
        void success4() throws Exception {
            //given
            when(adminService.deleteUser(any(), any())).thenReturn("사원 삭제 성공");

            //when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                    .delete("/admin/users/1")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("사원 삭제 성공"))
                    .andDo(document("delete-user",
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