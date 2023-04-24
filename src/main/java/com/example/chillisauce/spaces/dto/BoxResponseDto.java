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
    private final String boxName;
    private String x;
    private String y;



    public BoxResponseDto(Box box) {
        this.boxId = box.getId();
        this.boxName = box.getLocationName();
        this.x = box.getX();
        this.y = box.getY();
    }

}
