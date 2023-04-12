package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.MultiBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MultiBoxResponseDto {
    private final Long multiBoxId;
    private final String multiBoxName;
    private final String username;
    private String x;
    private String y;

    public MultiBoxResponseDto(MultiBox multiBox) {
        this.multiBoxId = multiBox.getId();
        this.multiBoxName = multiBox.getMultiBoxName();
        this.username = multiBox.getUsername();
        this.x = multiBox.getX();
        this.y = multiBox.getY();
    }

}
