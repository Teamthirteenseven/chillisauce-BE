package com.example.chillisauce.domain.spaces.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MultiBoxRequestDto {


    private String multiBoxName;

    private String x;

    private String y;



    public MultiBoxRequestDto(String locationName, String x, String y) {
        this.multiBoxName = locationName;
        this.x = x;
        this.y = y;
    }
}
