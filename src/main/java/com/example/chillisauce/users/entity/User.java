package com.example.chillisauce.users.entity;

import com.example.chillisauce.users.dto.AdminSignupRequestDto;
import com.example.chillisauce.users.dto.UserSignupRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = true, unique = true)
    private String companyName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = true)
    private String certification;

    //관리자 회원가입용 생성자
    public User(AdminSignupRequestDto adminSignupRequestDto, String password, UserRoleEnum role) {
        this.email = adminSignupRequestDto.getEmail();
        this.password = password;   //패스워드인코더 사용
        this.username = adminSignupRequestDto.getUserName();
        this.companyName = adminSignupRequestDto.getCompanyName();
        this.role = role;
    }
    //사원 회원가입용 생성자
    public User(UserSignupRequestDto userSignupRequestDto, String password, UserRoleEnum role) {
        this.email = userSignupRequestDto.getEmail();
        this.password = password;   //패스워드인코더 사용
        this.username = userSignupRequestDto.getUserName();
        this.certification = userSignupRequestDto.getCertification();
        this.role = role;
    }
}
