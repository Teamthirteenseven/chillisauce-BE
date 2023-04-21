package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Mr;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MrResponseDto {
    private final Long mrId;
    private final String locationName;
    private String x;

    private String y;

    public MrResponseDto(Mr mr) {
        this.mrId = mr.getId();
        this.locationName = mr.getLocationName();
        this.x = mr.getX();
        this.y = mr.getY();
    }


}

