package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.MultiBox;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class MultiBoxRepositoryTest {
    @Autowired
    private MultiBoxRepository multiBoxRepository;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @DisplayName("박스 저장")
        @Test
        void addmultiBox() {
            //given
            MultiBox multiBox = MultiBox.builder()
                    .multiBoxName("테스트")
                    .id(1L)
                    .x("900")
                    .y("800").build();

            //when
            MultiBox saveBox = multiBoxRepository.save(multiBox);

            //then
            Assertions.assertThat(saveBox.getMultiBoxName()).isEqualTo(multiBox.getMultiBoxName());
            Assertions.assertThat(saveBox.getId()).isEqualTo(multiBox.getId());
            Assertions.assertThat(saveBox.getX()).isEqualTo(multiBox.getX());
            Assertions.assertThat(saveBox.getY()).isEqualTo(multiBox.getY());

        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Nested
        @DisplayName("Null")
        class failMultiBox {
            @DisplayName("MultiBox정보가 Null인 경우")
            @Test
            void fail1() {
                // given
                final MultiBox multiBox = MultiBox.builder()
                        .multiBoxName(null)
                        .x(null)
                        .y(null)
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> multiBoxRepository.save(multiBox));
            }

            @DisplayName("MultiBox정보가 빈 문자열인 경우")
            @Test
            void fail2() {
                // given
                final MultiBox multiBox = MultiBox.builder()
                        .multiBoxName("")
                        .x("")
                        .y("")
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> multiBoxRepository.save(multiBox));
            }
        }
    }
}
