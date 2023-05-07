package com.example.chillisauce.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupRequestDto {
    @NotBlank
    @Email(message = "이메일 형식이 올바르지 않습니다.",
            regexp = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,16}$",
            message = "비밀번호는 8 ~ 16자리 영문, 숫자, 특수문자를 조합하여 입력하세요.")
    private String password;
    private String passwordCheck;
    private String username;
    private String certification;

}
