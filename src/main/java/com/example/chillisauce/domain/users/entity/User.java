package com.example.chillisauce.domain.users.entity;

import com.example.chillisauce.domain.users.dto.request.AdminSignupRequestDto;
import com.example.chillisauce.domain.users.dto.request.UserSignupRequestDto;
import com.example.chillisauce.domain.users.dto.request.RoleDeptUpdateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username")
})
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
    public User(AdminSignupRequestDto adminSignupRequestDto, String password, UserRoleEnum role, Companies companies) {
        this.email = adminSignupRequestDto.getEmail();
        this.password = password;   //PasswordEncoder
        this.username = adminSignupRequestDto.getUsername();
        this.role = role;
        this.companies = companies;
    }

    //일반사원 회원가입용 생성자
    public User(UserSignupRequestDto userSignupRequestDto, String password, UserRoleEnum role, Companies companies) {
        this.email = userSignupRequestDto.getEmail();
        this.password = password;   //PasswordEncoder
        this.username = userSignupRequestDto.getUsername();
        this.role = role;
        this.companies = companies;
    }


    public void update(RoleDeptUpdateRequestDto requestDto) {
        this.role = requestDto.getRole();
    }

}
