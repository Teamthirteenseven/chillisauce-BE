package com.example.chillisauce.users.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.dto.response.UserDetailResponseDto;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchService 클래스")
class SearchServiceTest {
    @InjectMocks
    SearchService searchService;

    @Mock
    UserRepository userRepository;

    @Nested
    @DisplayName("searchUser 메서드는")
    class SearchUserTestCase {
        // given
        String query = "홍";
        Companies company = Companies.builder().companyName("testCompany").build();
        User user = User.builder().email("tester@test.com").companies(company).username("test").build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getEmail());
        User findOne = User.builder().companies(company).username("홍길동").build();
        User findTwo = User.builder().companies(company).username("안재홍").build();

        @Test
        void 문자열에_포함된_유저를_반환한다() {
            // given
            when(userRepository.findAllByUsernameContainingAndCompanies(eq(query), eq(company.getCompanyName())))
                    .thenReturn(List.of(findOne, findTwo));

            // when
            List<UserDetailResponseDto> result = searchService.searchUser(query, userDetails);

            // then
            Assertions.assertThat(result).filteredOn(x -> x.getUsername().contains(query)).hasSize(2);
        }
    }
}