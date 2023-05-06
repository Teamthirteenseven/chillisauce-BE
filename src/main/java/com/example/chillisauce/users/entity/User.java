package com.example.chillisauce.users.entity;

import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.users.dto.AdminSignupRequestDto;
import com.example.chillisauce.users.dto.RoleDeptUpdateRequestDto;
import com.example.chillisauce.users.dto.UserSignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
/* 성능테스트 1. 인덱싱 / 논인덱싱 비교*/
//@Table(name = "users", indexes = {
//        @Index(name = "idx_id", columnList = "id", unique = true),
//        @Index(name = "idx_email", columnList = "email", unique = true)
//})
/* 성능테스트 1. 인덱싱 / 논인덱싱 비교*/
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        this.username = adminSignupRequestDto.getUsername();
        this.role = role;
        this.companies = companies;
    }
    //일반사원 회원가입용 생성자
    public User(UserSignupRequestDto userSignupRequestDto, String password,  UserRoleEnum role, Companies companies) {
        this.email = userSignupRequestDto.getEmail();
        this.password = password;   //패스워드인코더 사용
        this.username = userSignupRequestDto.getUsername();
        this.role = role;
        this.companies = companies;
    }

    //업데이트 부분은 나중에 추가될 수 있음
    public void update(RoleDeptUpdateRequestDto requestDto) {
        this.role = requestDto.getRole();
//        this.dept = requestDto.getDept(); //부서명 컬럼에 대한 확장성을 대비
    }

}
