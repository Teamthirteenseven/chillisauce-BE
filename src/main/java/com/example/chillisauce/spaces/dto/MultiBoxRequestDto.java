package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MultiBoxRequestDto {


    private String multiBoxName;

    private String x;

    private String y;

    public MultiBoxRequestDto(LocationDto locationDto) {
        this.multiBoxName = locationDto.getLocationName();
        this.x = locationDto.getX();
        this.y = locationDto.getY();
    }

    public MultiBoxRequestDto(String locationName, String x, String y) {
        this.multiBoxName = locationName;
        this.x = x;
        this.y = y;
    }
}
