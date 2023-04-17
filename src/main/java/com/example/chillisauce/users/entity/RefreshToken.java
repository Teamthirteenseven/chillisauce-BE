package com.example.chillisauce.users.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 500)
    private String refreshToken;

    @NotBlank
    private String email;

    public RefreshToken(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}