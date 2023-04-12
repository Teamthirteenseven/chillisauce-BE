package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Floor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FloorRequestDto {

    private String floorName;
    public FloorRequestDto(String floorName) {
        this.floorName = floorName;
    }

}
