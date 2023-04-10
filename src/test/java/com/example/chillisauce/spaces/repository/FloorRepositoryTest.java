package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Floor;
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
@DisplayName("floor Test")
@DataJpaTest
class FloorRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private FloorRepository floorRepository;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @DisplayName("Space 저장")
        @Test
        void addFloor() {
            //given
            Companies companies = Companies.builder()
                    .certification("abcd")
                    .companyName("테스트")
                    .build();
            Floor floor = Floor.builder()
                    .floorName("테스트")
                    .companies(companies)
                    .build();



            //when
            Companies saveCompanies = companyRepository.save(companies);
            Floor saveFloor = floorRepository.save(floor);

            //then
            Assertions.assertThat(saveCompanies.getCertification()).isEqualTo(companies.getCertification());
            Assertions.assertThat(saveCompanies.getCompanyName()).isEqualTo(companies.getCompanyName());
            Assertions.assertThat(saveFloor.getFloorName()).isEqualTo(floor.getFloorName());

        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("Null")
            class NullFloor {
                @DisplayName("floor정보가 Null인 경우")
                @Test
                void fail2() {
                    // given
                    final Floor floor = Floor.builder()
                            .floorName(null)
                            .build();
                    //when
                    assertThrows(ConstraintViolationException.class,
                            () -> floorRepository.save(floor));
                }

                @DisplayName("Box정보가 빈 문자열인 경우")
                @Test
                void fail3() {
                    // given
                    final Floor floor = Floor.builder()
                            .floorName("")
                            .build();
                    //when
                    assertThrows(ConstraintViolationException.class,
                            () -> floorRepository.save(floor));
                }
            }
        }
    }
}