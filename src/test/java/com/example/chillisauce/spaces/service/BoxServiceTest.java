package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.BoxRepository;
import com.example.chillisauce.spaces.SpaceRepository;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest

class BoxServiceTest {

    @Autowired
    private BoxService boxService;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;


    @DisplayName("박스 생성")
    @Test
    void createBox() {
        Companies companies = Companies.builder()
                .companyName("호랑이")
                .certification("CERTIFIED")
                .build();
        companyRepository.save(companies);

        User user = User.builder()
                .id(0L)
                .email("tiger@tiger")
                .username("")
                .password("test")
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        userRepository.save(user);

        UserDetailsImpl details = new UserDetailsImpl(user, null);

        Space space = Space.builder()
                .id(1L)
                .spaceName("박스 생성 테스트")
                .companies(companies)
                .build();
        spaceRepository.save(space);

        Box box = Box.builder()
                .boxName("이민재자리")
                .x("777")
                .y("777")
                .space(space)
                .build();
        boxRepository.save(box);

        //given
        BoxRequestDto boxRequestDto = new BoxRequestDto("이민재자리", "777", "777");
        BoxResponseDto boxResponseDto = boxService.createBox("호랑이", 1L, boxRequestDto, details);
        //then
        Assertions.assertEquals("이민재자리",boxResponseDto.getBoxName());
        Assertions.assertEquals("777",boxResponseDto.getX());
        Assertions.assertEquals("777",boxResponseDto.getY());

    }


    @DisplayName("박스 개별 수정")
    @Test
    void updateBoxTest() {
        //given
        Companies companies = Companies.builder()
                .companyName("고양이")
                .certification("ABCD")
                .build();
        companyRepository.save(companies);

        User user = User.builder()
                .email("cat@cat")
                .username("")
                .password("test1")
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        userRepository.save(user);

        UserDetailsImpl details = new UserDetailsImpl(user, null);

        Space space = Space.builder()
                .spaceName("박스 업데이트 테스트")
                .companies(companies)
                .build();
        spaceRepository.save(space);

        Box box = Box.builder()
                .boxName("장혁진자리")
                .x("777")
                .y("777")
                .user(user)
                .space(space)
                .build();
        boxRepository.save(box);

        BoxRequestDto boxRequestDto = new BoxRequestDto("장혁진자리_수정", "888", "888");

        //when
        BoxResponseDto boxResponseDto = boxService.updateBox("고양이",box.getId(), boxRequestDto, details);

        //then
        Assertions.assertEquals("장혁진자리_수정",boxResponseDto.getBoxName());
        Assertions.assertEquals("888",boxResponseDto.getX());
        Assertions.assertEquals("888",boxResponseDto.getY());
    }

    @DisplayName("박스 삭제")
    @Test
    void deleteBoxTest() {
        //given
        Companies companies = Companies.builder()
                .companyName("강아지")
                .certification("AB")
                .build();
        companyRepository.save(companies);

        User user = User.builder()
                .email("dog@dog")
                .username("")
                .password("dog")
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        userRepository.save(user);

        UserDetailsImpl details = new UserDetailsImpl(user, null);

        Space space = Space.builder()
                .spaceName("박스 업데이트 테스트")
                .companies(companies)
                .build();
        spaceRepository.save(space);

        Box box = Box.builder()
                .boxName("장혁진자리")
                .x("777")
                .y("777")
                .user(user)
                .space(space)
                .build();
        boxRepository.save(box);

        BoxRequestDto boxRequestDto = new BoxRequestDto("장혁진자리", "888", "888");

        //when
        BoxResponseDto deleteBox = boxService.updateBox("강아지",box.getId(), boxRequestDto, details);

        //then
        Assertions.assertEquals("장혁진자리",deleteBox.getBoxName());
        Assertions.assertEquals("888",deleteBox.getX());
        Assertions.assertEquals("888",deleteBox.getY());
    }
}

