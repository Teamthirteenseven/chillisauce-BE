package com.example.chillisauce.jwt;

import com.example.chillisauce.security.UserDetailsServiceImpl;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.example.chillisauce.fixture.FixtureFactory.Company_생성;
import static com.example.chillisauce.fixture.FixtureFactory.User_USER권한_생성_아이디지정;
import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil 클래스")
class JwtUtilTest {
    private String secretKey = "22be42acccf5c7637400829e6dc8b4e1a0572955db912d7a972f4e614bc574956acebbb9bd88c38038c05493bf3978c3abe45c3646b2bbd0d217e0c907e311f6";


    @Nested
    @DisplayName("createToken 메서드는")
    class CreateTokenTestCase {
//        // given
//        Companies company = Company_생성();
//        User user = User_USER권한_생성_아이디지정(1L, company);
//
//        @Test
//        void 토큰을_생성한다() {
//            // when
//            String token = jwtUtil.createToken(user);
//
//            // then
//            assertThat(token).isNotNull();
//        }
    }

    @Nested
    @DisplayName("getHeaderToken 메서드는")
    class GetTokenTestCase {
        @Test
        void 헤더로부터_토큰을_반환한다() {

        }
    }

    @Nested
    @DisplayName("validateToken 메서드는")
    class ValidateTokenTestCase {
        @Test
        void 유효한_토큰이면_참을_반환한다() {

        }
    }

    @Nested
    @DisplayName("getUserInfoFromToken 메서드는")
    class GetUserInfoTestCase {
        @Test
        void 토큰으로부터_유저의_정보를_반환한다() {

        }
    }


    @Nested
    @DisplayName("createAuthentication 메서드는")
    class CreateAuthenticationTestCase {
        @Test
        void 인증객체를_생성한다() {

        }
    }

    @Nested
    @DisplayName("getAccessTime 메서드는")
    class getAccessTime {
        @Test
        void 액세스_토큰의_만료_시간을_반환한다() {

        }
    }
}