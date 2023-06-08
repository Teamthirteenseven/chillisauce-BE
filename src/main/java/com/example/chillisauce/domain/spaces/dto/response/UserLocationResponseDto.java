package com.example.chillisauce.domain.spaces.dto.response;

import com.example.chillisauce.domain.spaces.entity.UserLocation;
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
