package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Mr;
import lombok.Getter;

@Getter
public class MrResponseDto {
    private final Long id;
    private final String mrName;

    private final String username;

    private String x;

    private String y;


    public MrResponseDto(Mr mr) {
        this.id = mr.getId();
        this.mrName = mr.getMrName();
        this.username = mr.getUsername();
        this.x = mr.getX();
        this.y = mr.getY();
    }
}

