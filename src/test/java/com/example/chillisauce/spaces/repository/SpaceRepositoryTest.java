package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.repository.CompanyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
@Nested
@DisplayName("space Test")
@DataJpaTest
class SpaceRepositoryTest {
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @DisplayName("Space 저장")
        @Test
        void addSpace() {
            //given
            Companies companies = Companies.builder()
                    .certification("abcd")
                    .companyName("테스트")
                    .build();
            Space space = Space.builder()
                    .spaceName("테스트")
                    .companies(companies)
                    .floor(null)
                    .build();

            //when
            Companies saveCompanies = companyRepository.save(companies);
            Space saveSpace = spaceRepository.save(space);

            //then
            Assertions.assertThat(saveCompanies.getCertification()).isEqualTo(companies.getCertification());
            Assertions.assertThat(saveCompanies.getCompanyName()).isEqualTo(companies.getCompanyName());
            Assertions.assertThat(saveSpace.getSpaceName()).isEqualTo(space.getSpaceName());

        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Nested
        @DisplayName("Null")
        class NullSpace {
            @DisplayName("Space정보가 Null인 경우")
            @Test
            void fail2() {
                // given
                final Space space = Space.builder()
                        .spaceName(null)
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> spaceRepository.save(space));
            }

            //
            @DisplayName("space정보가 빈 문자열인 경우")
            @Test
            void fail3() {
                // given
                final Space space = Space.builder()
                        .spaceName("")
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> spaceRepository.save(space));

            }
        }
    }
}