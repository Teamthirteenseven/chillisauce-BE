package com.example.chillisauce.users.util;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
//@RequiredArgsConstructor
public class SuperuserInjector {
//    private final UserRepository userRepository;
//
//    @Value("${spring.security.user.password}")
//    String superuserPassword;

    // actuator 관리자 계정 생성 - 있으면 생성 X
//    @PostConstruct
//    public void injectSuperuser() throws UserException {
//
//        if(userRepository.findByEmail("systemAdmin").isPresent()){
//            return;
//        }
//
//        User superUser = User.builder()
//                .username("SYSADMIN")
//                .email("systemAdmin")
////                .password(superuserPassword)
//                .role(UserRoleEnum.SUPERUSER)
//                .companies(null)
//                .build();
//
//        userRepository.save(superUser);
//    }
}
