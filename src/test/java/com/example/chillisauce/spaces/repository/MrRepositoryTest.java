package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Mr;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class MrRepositoryTest {
    @Autowired
    private MrRepository mrRepository;
    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @DisplayName("박스 저장")
        @Test
        void addMr() {
            //given
            Mr mr = Mr.builder()
                    .mrName("테스트")
                    .x("900")
                    .y("800").build();

            //when
            Mr saveMr = mrRepository.save(mr);

            //then
            Assertions.assertThat(saveMr.getMrName()).isEqualTo(mr.getMrName());
            Assertions.assertThat(saveMr.getX()).isEqualTo(mr.getX());
            Assertions.assertThat(saveMr.getY()).isEqualTo(mr.getY());

        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Nested
        @DisplayName("Null")
        class NullMr {
            @DisplayName("Mr정보가 Null인 경우")
            @Test
            void fail2() {
                // given
                final Mr mr = Mr.builder()
                        .mrName(null)
                        .x(null)
                        .y(null)
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> mrRepository.save(mr));
            }

            @DisplayName("Box정보가 빈 문자열인 경우")
            @Test
            void fail3() {
                // given
                final Mr mr = Mr.builder()
                        .mrName("")
                        .x("")
                        .y("")
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> mrRepository.save(mr));
            }
        }
    }
}

