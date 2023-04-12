package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BoxRequestDto {


    private String boxName;

    private String x;

    private String y;

    public BoxRequestDto(String boxName, String x, String y) {
        this.boxName = boxName;
        this.x = x;
        this.y = y;
    }


}