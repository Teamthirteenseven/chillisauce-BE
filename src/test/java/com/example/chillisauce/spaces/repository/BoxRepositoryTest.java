package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.BoxRepository;
import com.example.chillisauce.spaces.entity.Box;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;


@DataJpaTest
class BoxRepositoryTest {
    @Autowired
    private BoxRepository boxRepository;

    @DisplayName("박스 저장")
    @Test
    void addBox() {
        //given
        Box box = Box.builder()
                .boxName("테스트")
                .id(1L)
                .x("900")
                .y("800").build();

        //when
        Box saveBox = boxRepository.save(box);

        //then
        Assertions.assertThat(saveBox.getBoxName()).isEqualTo(box.getBoxName());
        Assertions.assertThat(saveBox.getId()).isEqualTo(box.getId());
        Assertions.assertThat(saveBox.getX()).isEqualTo(box.getX());
        Assertions.assertThat(saveBox.getY()).isEqualTo(box.getY());

    }
//    @Test
//    void NotsaveBox() {
//        //given
//        Box box = Box.builder()
//                .boxName("테스트2")
//                .id(1l)
//                .x("200")
//                .y("200").build();
//
//        Box box2 = Box.builder()
//                .boxName("테스트2")
//                .id(1l)
//                .x("200")
//                .y("200").build();
//        //when, then
//        Assertions.assertThatThrownBy(() -> boxRepository.save(box2))
//                .isInstanceOf(ConstraintViolationException.class);
//    }
//
//    @Test
//    void NotBox() {
//        //given
//        Box box = Box.builder()
//                .boxName("")
//                .id(1l)
//                .x("")
//                .y("").build();
//
//        Assertions.assertThatThrownBy(() -> boxRepository.save(box))
//                .isInstanceOf(DataIntegrityViolationException.class);
//    }
//
}

