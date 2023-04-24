package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BoxRequestDto {

    private String boxName;
    private String x;
    private String y;


    public BoxRequestDto(LocationDto locationDto) {
        this.boxName = locationDto.getLocationName();
        this.x = locationDto.getX();
        this.y = locationDto.getY();
    }

    public BoxRequestDto(String locationName, String x, String y) {
        this.boxName = locationName;
        this.x = x;
        this.y = y;
    }
}