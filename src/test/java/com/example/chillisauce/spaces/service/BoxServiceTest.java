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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest

class BoxServiceTest {

    @Autowired
    private BoxService boxService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        Companies companies = Companies.builder()
                .companyName("호랑이")
                .certification("CERTIFIED")
                .build();
        companyRepository.save(companies);

        User user = User.builder()
                .username("이민재")
                .password("test")
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        userRepository.save(user);
        UserDetailsImpl details = new UserDetailsImpl(user, user.getUsername());


        Space space = Space.builder()
                .spaceName("테스트 공간")
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


    }

//    @DisplayName("박스 생성")
//    @Test
//    void createBox() {
//        //given
//        BoxRequestDto boxRequestDto = new BoxRequestDto("이민재자리", "777", "777");
//        Space space = spaceService.findCompanyNameAndSpaceId("호랑이", 1l);

//
//        UserDetailsImpl details = new UserDetailsImpl(user, user.getUsername());

        //when
//        BoxResponseDto boxResponseDto = boxService.createBox("호랑이",1l,boxRequestDto,details);
//
//        //then
//        Assertions.assertThat(boxResponseDto.getBoxName()).isEqualTo(boxRequestDto.getBoxName());
//        Assertions.assertThat(boxResponseDto.getX()).isEqualTo(boxRequestDto.getX());
//        Assertions.assertThat(boxResponseDto.getY()).isEqualTo(boxRequestDto.getY());
//    }


}

