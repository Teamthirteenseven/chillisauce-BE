package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Mr;
import lombok.Getter;

@Getter
public class MrResponseDto {
    private final Long mrId;
    private final String mrName;

    private final String username;

    private String x;

    private String y;

    public MrResponseDto(Mr mr) {
        this.mrId = mr.getId();
        this.mrName = mr.getMrName();
        this.username = mr.getUsername();
        this.x = mr.getX();
        this.y = mr.getY();
    }
}

