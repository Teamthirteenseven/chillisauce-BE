package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Getter
@Entity
@RequiredArgsConstructor
public class Mr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mrName;

    private String x;

    private String y;

    private String username;


    public Mr(MrRequestDto mrRequestDto, User user) {
        this.mrName = mrRequestDto.getMrName();
        this.x = mrRequestDto.getX();
        this.y = mrRequestDto.getY();
        this.username = user.getUsername();
    }

}
