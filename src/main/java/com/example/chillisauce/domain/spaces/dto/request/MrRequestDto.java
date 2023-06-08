package com.example.chillisauce.domain.spaces.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MrRequestDto {
    private String mrName;
    private String x;
    private String y;


    public MrRequestDto(String locationName, String x, String y) {
        this.mrName = locationName;
        this.x = x;
        this.y = y;
    }
}

