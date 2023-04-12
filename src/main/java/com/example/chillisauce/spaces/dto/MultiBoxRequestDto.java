package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MultiBoxRequestDto {


    private String multiBoxName;

    private String x;

    private String y;

    public MultiBoxRequestDto(String multiBoxName, String x, String y) {
        this.multiBoxName = multiBoxName;
        this.x = x;
        this.y = y;
    }

}
