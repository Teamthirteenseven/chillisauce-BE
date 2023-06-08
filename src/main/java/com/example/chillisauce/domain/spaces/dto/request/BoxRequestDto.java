package com.example.chillisauce.domain.spaces.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class BoxRequestDto {

    private String boxName;
    private String x;
    private String y;




    public BoxRequestDto(String locationName, String x, String y) {
        this.boxName = locationName;
        this.x = x;
        this.y = y;
    }
}