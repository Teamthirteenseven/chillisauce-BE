package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.UserLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDto {
    private Long id;
    private String locationName;
    private String x;
    private String y;

    private String username;


    public LocationDto(Location location) {
        this.id = location.getId();
        this.locationName = location.getLocationName();
        this.x = location.getX();
        this.y = location.getY();
    }

    public LocationDto(long id, String locationName, String x, String y) {
        this.id = id;
        this.locationName = locationName;
        this.x = x;
        this.y = y;
    }
}
