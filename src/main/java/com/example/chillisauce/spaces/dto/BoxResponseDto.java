package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Box;
import lombok.Getter;

@Getter
public class BoxResponseDto {
    private final Long boxId;
    private final String boxName;
    private final String username;
    private String x;
    private String y;

    public BoxResponseDto(Box box) {
        this.boxId = box.getId();
        this.boxName = box.getBoxName();
        this.username = box.getUsername();
        this.x = box.getX();
        this.y = box.getY();
    }
}
