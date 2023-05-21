package com.example.chillisauce.jwt;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.security.UserDetailsServiceImpl;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static com.example.chillisauce.fixture.FixtureFactory.Company_생성;
import static com.example.chillisauce.fixture.FixtureFactory.User_USER권한_생성_아이디지정;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("JwtUtil 클래스")
class JwtUtilTest {
    private final String secretKey = "22be42acccf5c7637400829e6dc8b4e1a0572955db912d7a972f4e614bc574956acebbb9bd88c38038c05493bf3978c3abe45c3646b2bbd0d217e0c907e311f6";
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil(secretKey, userDetailsService);
    }

    @Nested
    @DisplayName("createToken 메서드는")
    class CreateTokenTestCase {
        // given
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);

        @Test
        void 토큰을_생성한다() {
            // when
            String token = jwtUtil.createToken(user);
            // then
            assertThat(token).isNotNull();
        }
    }

    @Nested
    @DisplayName("getHeaderToken 메서드는")
    class GetTokenTestCase {
        // given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);

        @Test
        void 헤더로부터_토큰을_반환한다() {
            // given
            String token = jwtUtil.createToken(user);
            mockRequest.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            // when
            String result = jwtUtil.getHeaderToken(mockRequest);

            // then
            String subject = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                    .build().parseClaimsJws(result).getBody().getSubject();

            assertThat(result).isNotNull();
            assertThat(subject).isEqualTo(user.getEmail());
        }

        @Test
        void 비어있는_문자열이_들어오면_null_반환한다() {
            // given
            String token = "";
            mockRequest.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            // when
            String result = jwtUtil.getHeaderToken(mockRequest);

            // then
            assertThat(result).isNull();
        }

        @Test
        void prefix_일치하지_않으면_null_반환한다() {
            // given
            Date date = new Date();
            String token = "WrongPrefix " + Jwts.builder()
                    .setSubject("testSubject")
                    .claim("test@test.com", user.getEmail())
                    .setExpiration(new Date(date.getTime() + jwtUtil.getAccessTime()))
                    .setIssuedAt(date)
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256)
                    .compact();
            mockRequest.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            // when
            String result = jwtUtil.getHeaderToken(mockRequest);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("validateToken 메서드는")
    class ValidateTokenTestCase {
        // given
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);

        @Test
        void 유효한_토큰이_들어오면_true_반환한다() {
            // given
            Date date = new Date();
            String token = Jwts.builder()
                    .setSubject("testSubject")
                    .claim("test@test.com", user.getEmail())
                    .setExpiration(new Date(date.getTime() + jwtUtil.getAccessTime()))
                    .setIssuedAt(date)
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256)
                    .compact();

            boolean result = jwtUtil.validateToken(token);

            assertThat(result).isTrue();
        }

        @Test
        void 만료된_토큰이_들어오면_예외를_반환한다() {
            // given
            Date date = new Date();
            String token = Jwts.builder()
                    .setSubject("testSubject")
                    .claim("test@test.com", user.getEmail())
                    .setExpiration(new Date(date.getTime() - 1))
                    .setIssuedAt(date)
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256)
                    .compact();

            boolean result = jwtUtil.validateToken(token);

            assertThat(result).isFalse();
        }

        @Test
        void 유효하지_않은_형식의_토큰을_받으면_예외를_반환한다() {
            // given
            String token = "12345";

            boolean result = jwtUtil.validateToken(token);

            assertThat(result).isFalse();
        }

        @Test
        void 지원되지_않는_형식의_토큰을_받으면_예외를_반환한다() {
            // given
            Date date = new Date();

            String token = Jwts.builder()
                    .setExpiration(new Date(date.getTime() + jwtUtil.getAccessTime()))
                    .compact();

            boolean result = jwtUtil.validateToken(token);

            assertThat(result).isFalse();
        }

        @Test
        void 비어있는_문자열이면_예외가_발생한다() {
            // given
            String token = " ";

            boolean result = jwtUtil.validateToken(token);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getUserInfoFromToken 메서드는")
    class GetUserInfoTestCase {
        // given
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);

        @Test
        void 토큰으로부터_유저의_정보를_반환한다() {
            // given
            Date date = new Date();
            String token = Jwts.builder()
                    .setSubject("testSubject")
                    .claim("test@test.com", user.getEmail())
                    .setExpiration(new Date(date.getTime() + jwtUtil.getAccessTime()))
                    .setIssuedAt(date)
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256)
                    .compact();

            // when
            String result = jwtUtil.getUserInfoFromToken(token);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("testSubject");
        }
    }


    @Nested
    @DisplayName("createAuthentication 메서드는")
    class CreateAuthenticationTestCase {
        // given
        Companies company = Company_생성();
        User user = User_USER권한_생성_아이디지정(1L, company);

        @Test
        void 인증객체를_생성한다() {
            // given
            String email = user.getEmail();
            UserDetailsImpl userDetails = new UserDetailsImpl(user, user.getUsername());
            when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

            // when
            Authentication result = jwtUtil.createAuthentication(email);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isAuthenticated()).isTrue();
        }
    }

    @Nested
    @DisplayName("getAccessTime 메서드는")
    class getAccessTime {
        @Test
        void 액세스_토큰의_만료_시간을_반환한다() {
            long accessTime = jwtUtil.getAccessTime();

            assertThat(accessTime).isEqualTo(8 * 60 * 60 * 1000L);
        }
    }
}