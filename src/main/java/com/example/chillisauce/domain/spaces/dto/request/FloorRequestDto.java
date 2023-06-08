package com.example.chillisauce.domain.spaces.dto.request;

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
