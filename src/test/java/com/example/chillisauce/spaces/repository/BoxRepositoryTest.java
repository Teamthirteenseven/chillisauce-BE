package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Location;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
class BoxRepositoryTest {
    @Autowired
    private BoxRepository boxRepository;
    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @DisplayName("박스 저장")
        @Test
        void addBox() {
            //given
            Box box = (Box) Box.builder()
                    .locationName("테스트")
                    .x("900")
                    .y("800").build();

            //when
            Box saveBox = boxRepository.save(box);

            //then
            Assertions.assertThat(saveBox.getLocationName()).isEqualTo(box.getLocationName());
            Assertions.assertThat(saveBox.getX()).isEqualTo(box.getX());
            Assertions.assertThat(saveBox.getY()).isEqualTo(box.getY());

        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Nested
        @DisplayName("Null")
        class NullBox {
            @DisplayName("Box정보가 Null인 경우")
            @Test
            void fail2() {
                // given
                final Box box = Box.builder()
                        .locationName(null)
                        .x(null)
                        .y(null)
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> boxRepository.save(box));
            }

            @DisplayName("Box정보가 빈 문자열인 경우")
            @Test
            void fail3() {
                // given
                final Box box = Box.builder()
                        .locationName("")
                        .x("")
                        .y("")
                        .build();
                //when
                assertThrows(ConstraintViolationException.class,
                        () -> boxRepository.save(box));
            }
        }
    }
}

