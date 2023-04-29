package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.UserLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiBoxResponseDto {
    private Long multiBoxId;
    private String multiBoxName;
    private String x;
    private String y;
    private List<UserLocationResponseDto> userlist;
    public MultiBoxResponseDto(MultiBox multiBox, List<UserLocation> userLocations) {
        this.multiBoxId = multiBox.getId();
        this.multiBoxName = multiBox.getLocationName();
        this.x = multiBox.getX();
        this.y = multiBox.getY();
        this.userlist = userLocations.stream()
                .map(UserLocationResponseDto::new)
                .collect(Collectors.toList());
    }

    public MultiBoxResponseDto(MultiBox multiBox) {
        this.multiBoxId = multiBox.getId();
        this.multiBoxName = multiBox.getLocationName();
        this.x = multiBox.getX();
        this.y = multiBox.getY();


    }
}
