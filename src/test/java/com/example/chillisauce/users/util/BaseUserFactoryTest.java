package com.example.chillisauce.users.util;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.chillisauce.fixture.FixtureFactory.Company_생성;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BaseUserFactory 클래스")
class BaseUserFactoryTest {
    BaseUserFactory baseUserFactory;
    @Mock
    CompanyRepository companyRepository;
    @Mock
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        baseUserFactory = new BaseUserFactory(companyRepository, passwordEncoder, userRepository);
    }

    @Nested
    @DisplayName("makeBaseUser 메서드는")
    class MakeBaseUserTestCase {
        // given
        Companies company = Company_생성();
        @Test
        void 테스트용_기본_유저를_생성한다() {
            // given
            when(companyRepository.findByCompanyName(eq(company.getCompanyName())))
                    .thenReturn(Optional.of(company));

            // when
            baseUserFactory.makeBaseUser(company.getCompanyName());
        }
    }

}