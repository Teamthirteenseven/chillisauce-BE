package com.example.chillisauce.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String email;
    private String password;
    private String passwordCheck;
    private String userName;
    private String companyName;
    private String certification;
}
