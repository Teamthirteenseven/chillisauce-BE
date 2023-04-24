package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MrRequestDto {
    private String locationName;
    private String x;
    private String y;

    public MrRequestDto(LocationDto locationDto) {
        this.locationName = locationDto.getLocationName();
        this.x = locationDto.getX();
        this.y = locationDto.getY();
    }
    public MrRequestDto(String locationName, String x, String y) {
        this.locationName = locationName;
        this.x = x;
        this.y = y;
    }
}

