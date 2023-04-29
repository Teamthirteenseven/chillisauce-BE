package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.config.TestConfig;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.MultiBox;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
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
            MultiBox multiBox = (MultiBox) MultiBox.builder()
                    .locationName("테스트")
                    .x("900")
                    .y("800").build();

            //when
            MultiBox saveBox = multiBoxRepository.save(multiBox);

            //then
            Assertions.assertThat(saveBox.getLocationName()).isEqualTo(multiBox.getLocationName());
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
                final MultiBox multiBox = (MultiBox) MultiBox.builder()
                        .locationName(null)
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
                final MultiBox multiBox = (MultiBox) MultiBox.builder()
                        .locationName("")
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
