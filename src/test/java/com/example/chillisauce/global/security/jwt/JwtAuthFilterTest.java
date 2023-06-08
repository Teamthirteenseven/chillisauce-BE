package com.example.chillisauce.global.security.jwt;

import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.entity.User;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.global.security.UserDetailsServiceImpl;
import com.example.chillisauce.global.security.jwt.JwtAuthFilter;
import com.example.chillisauce.global.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.example.chillisauce.fixture.FixtureFactory.Company_생성;
import static com.example.chillisauce.fixture.FixtureFactory.User_USER권한_생성_아이디지정;
import static org.mockito.Mockito.when;

@DisplayName("JwtAuthFilter 클래스")
class JwtAuthFilterTest {
    private final String secretKey = "22be42acccf5c7637400829e6dc8b4e1a0572955db912d7a972f4e614bc574956acebbb9bd88c38038c05493bf3978c3abe45c3646b2bbd0d217e0c907e311f6";
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    private JwtUtil jwtUtil;
    private JwtAuthFilter jwtAuthFilter;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil(secretKey, userDetailsService);
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);
    }

    @Nested
    @DisplayName("doFilterInternal 메서드는")
    class DoFilterInternalTestCase{
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response =new MockHttpServletResponse();
        MockFilterChain filterChain =new MockFilterChain();
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);

        @Test
        void 유효한_토큰이면_인증객체를_생성한다() throws ServletException, IOException {
            // given
            String token = jwtUtil.createToken(user);
            UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());
            request.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
            when(userDetailsService.loadUserByUsername(user.getEmail()))
                    .thenReturn(userDetails);

            // when
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        }

        @Test
        void 토큰이_없으면_예외를_반환한다() throws ServletException, IOException {
            // given
            request.addHeader(JwtUtil.AUTHORIZATION_HEADER, "Invalid Token");

            // when
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        }

        @Test
        void 유효하지_않은_토큰이면_예외를_반환한다() throws ServletException, IOException {
            // given
            String token = JwtUtil.BEARER_PREFIX + "invalidTokenString";
            request.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            // when
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        }
    }

}