package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Box {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String boxName;

    private String x;

    private String y;

    private String username;


    public Box(BoxRequestDto boxRequestDto, User user) {
        this.boxName = boxRequestDto.getBoxName();
        this.x = boxRequestDto.getX();
        this.y = boxRequestDto.getY();
        this.username = user.getUsername();
    }



}
