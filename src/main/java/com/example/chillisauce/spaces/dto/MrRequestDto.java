package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MrRequestDto {



    private String mrName;
    private String x;
    private String y;

    public MrRequestDto(String mrName, String x, String y) {
        this.mrName = mrName;
        this.x = x;
        this.y = y;
    }

}

