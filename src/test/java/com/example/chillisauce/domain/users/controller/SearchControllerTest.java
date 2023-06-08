package com.example.chillisauce.domain.users.controller;

import com.example.chillisauce.domain.reservations.exception.ReservationExceptionHandler;
import com.example.chillisauce.domain.users.controller.SearchController;
import com.example.chillisauce.domain.users.dto.response.UserDetailResponseDto;
import com.example.chillisauce.domain.users.entity.UserRoleEnum;
import com.example.chillisauce.domain.users.service.SearchService;
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

import java.util.List;

import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentRequest;
import static com.example.chillisauce.docs.ApiDocumentUtil.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@DisplayName("SearchController 클래스")
class SearchControllerTest {

    @InjectMocks
    SearchController searchController;

    @Mock
    SearchService searchService;


    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(searchController)
                .setControllerAdvice(new ReservationExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("유저 검색 GET 요청 시")
    class SearchUserTestCase {
        // given
        String url = "/users/search";

        List<UserDetailResponseDto> response = getUserList();

        @Test
        @WithMockUser
        void 문자열을_포함하는_유저목록을_반환한다() throws Exception {
            // given
            when(searchService.searchUser(any(), any())).thenReturn(response);

            // when
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .param("name", "홍")
                    .header("Authorization", "Bearer Token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("get-user-search-result",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과값"),
                                    fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                    fieldWithPath("data[].email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("data[].username").type(JsonFieldType.STRING).description("이름"),
                                    fieldWithPath("data[].role").type(JsonFieldType.STRING).description("권한")
                            )
                    ));
        }

        // 유저 검색 결과 반환
        private List<UserDetailResponseDto> getUserList() {
            UserDetailResponseDto userOne = UserDetailResponseDto.builder()
                    .email("test1@test1.com")
                    .role(UserRoleEnum.USER)
                    .username("홍길동")
                    .userId(1L).build();

            UserDetailResponseDto userTwo = UserDetailResponseDto.builder()
                    .email("test2@test2.com")
                    .role(UserRoleEnum.USER)
                    .username("안재홍").userId(2L).build();

            return List.of(userOne, userTwo);
        }
    }
}