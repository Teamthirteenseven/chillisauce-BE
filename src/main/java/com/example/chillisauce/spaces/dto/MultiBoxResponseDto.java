package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.MultiBox;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class MultiBoxResponseDto {
    private Long multiBoxId;
    private String multiBoxName;
    private String x;
    private String y;
    private String username;
    public MultiBoxResponseDto(MultiBox multiBox) {
        this.multiBoxId = multiBox.getId();
        this.multiBoxName = multiBox.getLocationName();
        this.x = multiBox.getX();
        this.y = multiBox.getY();
        this.username = getUsername();
    }


}
