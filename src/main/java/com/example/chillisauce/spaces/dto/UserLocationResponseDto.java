package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.UserLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLocationResponseDto {

    private String username;

    public UserLocationResponseDto(UserLocation userLocation) {
        this.username = userLocation.getUsername();
    }
}
