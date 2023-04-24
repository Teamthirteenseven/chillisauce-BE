package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MultiBoxRequestDto {


    private String locationName;

    private String x;

    private String y;

    public MultiBoxRequestDto(LocationDto locationDto) {
        this.locationName = locationDto.getLocationName();
        this.x = locationDto.getX();
        this.y = locationDto.getY();
    }

    public MultiBoxRequestDto(String locationName, String x, String y) {
        this.locationName = locationName;
        this.x = x;
        this.y = y;
    }
}
