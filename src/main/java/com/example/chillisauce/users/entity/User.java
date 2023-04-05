package com.example.chillisauce.users.entity;

import com.example.chillisauce.spaces.entity.Box;
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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;


    @ManyToOne
    @JoinColumn(name = "companies_id")
    private Companies companies;

    //관리자 회원가입용 생성자
    public User(AdminSignupRequestDto adminSignupRequestDto, String password,  UserRoleEnum role, Companies companies) {
        this.email = adminSignupRequestDto.getEmail();
        this.password = password;   //패스워드인코더 사용
        this.username = adminSignupRequestDto.getUserName();
        this.role = role;
        this.companies = companies;
    }
    //일반사원 회원가입용 생성자
    public User(UserSignupRequestDto userSignupRequestDto, String password,  UserRoleEnum role, Companies companies) {
        this.email = userSignupRequestDto.getEmail();
        this.password = password;   //패스워드인코더 사용
        this.username = userSignupRequestDto.getUserName();
        this.role = role;
        this.companies = companies;
    }


}
