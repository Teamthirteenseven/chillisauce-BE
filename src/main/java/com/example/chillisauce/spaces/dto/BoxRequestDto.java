package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BoxRequestDto {

    private Long id;

    private String boxName;

    private String x;

    private String y;

    private String username;

    public BoxRequestDto(String boxName, String x, String y) {
        this.boxName = boxName;
        this.x = x;
        this.y = y;
    }


}