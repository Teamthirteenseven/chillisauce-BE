package com.example.chillisauce.domain.spaces.dto.response;

import com.example.chillisauce.domain.spaces.entity.UserLocation;
import com.example.chillisauce.domain.spaces.entity.Box;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxResponseDto {
    private Long boxId;
    private String boxName;
    private String x;
    private String y;
    private String username;

    public BoxResponseDto(Box box) {
        this.boxId = box.getId();
        this.boxName = box.getLocationName();
        this.x = box.getX();
        this.y = box.getY();
    }


    public BoxResponseDto(Box box, UserLocation userLocation) {
        this.boxId = box.getId();
        this.boxName = box.getLocationName();
        this.x = box.getX();
        this.y = box.getY();
        this.username = userLocation != null ? userLocation.getUsername() : null;
    }




}
