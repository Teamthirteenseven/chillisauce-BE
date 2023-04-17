package com.example.chillisauce.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,16}$",
            message = "비밀번호는 8 ~ 16자리 영문, 숫자, 특수문자를 조합하여 입력하세요.")
    private String password;
}
