package com.example.chillisauce.security;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SuperuserInformation {
//    private final User superUser;

//    @Autowired
//    private SuperuserInformation(@Value("${spring.security.user.name}") String superusername,
//                                 @Value("${spring.security.user.password}") String superuserPassword,
//                                 @Value("${spring.security.user.roles}") UserRoleEnum superuserRole) {
//        this.superUser=User.builder()
//                .email(superusername)
//                .password(superuserPassword)
//                .role(superuserRole)
//                .build();
//    }

}
