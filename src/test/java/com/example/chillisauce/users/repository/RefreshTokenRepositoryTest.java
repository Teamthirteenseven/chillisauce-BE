package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Nested
@DisplayName("RefreshToken Test")
class RefreshTokenRepositoryTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @DisplayName("리프레쉬토큰 생성")
    @Test
    void refreshTest() {
        //given
        final RefreshToken refreshToken = RefreshToken.builder()
                .email("123")
                .refreshToken("1234")
                .build();

        //when
        RefreshToken saveRefresh = refreshTokenRepository.save(refreshToken);

        //then
        assertThat(saveRefresh.getId()).isNotNull();
        assertThat(saveRefresh.getEmail()).isEqualTo("123");
        assertThat(saveRefresh.getRefreshToken()).isEqualTo("1234");
    }

//    @Nested
//    @DisplayName("실패 케이스")
//    class FailCase {
//        @DisplayName("리프레시토큰 등록 실패(사용자 Null)")
//        @Test
//        void fail1() {
//            //given
//            final RefreshToken refreshToken = RefreshToken.builder()
//                    .email(null)
//                    .refreshToken("1234")
//                    .build();
//
//            final RefreshToken refreshToken2 = RefreshToken.builder()
//                    .email("123")
//                    .refreshToken("1234")
//                    .build();
//
//            //when
//            RefreshToken saveRefresh = refreshTokenRepository.save(refreshToken);
//
//            //then
////            assertThat(saveRefresh.getId()).isNotNull();
//            assertThat(saveRefresh.getEmail()).isEqualTo("공백일 수 없습니다");
//            assertThat(saveRefresh.getRefreshToken()).isEqualTo("1234");
//        }
//    }
}