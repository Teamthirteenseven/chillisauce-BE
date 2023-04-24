package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Box;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoxResponseDto {
    private Long boxId;
    private final String locationName;
    private String x;
    private String y;



    public BoxResponseDto(Box box) {
        this.boxId = box.getId();
        this.locationName = box.getLocationName();
        this.x = box.getX();
        this.y = box.getY();
    }

}
