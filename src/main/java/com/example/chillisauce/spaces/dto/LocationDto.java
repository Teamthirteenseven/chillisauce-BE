package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.UserLocation;
import com.example.chillisauce.users.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationDto {
    private Long locationId;
    private String locationName;
    private String x;
    private String y;

    private String username;


    public LocationDto(Location location, User user) {
        this.locationId = location.getId();
        this.locationName = location.getLocationName();
        this.x = location.getX();
        this.y = location.getY();
        this.username = user.getUsername();
    }

    public LocationDto(long id, String locationName, String x, String y) {
        this.locationId = id;
        this.locationName = locationName;
        this.x = x;
        this.y = y;
    }

    public LocationDto(Location location) {
        this.locationId = location.getId();
        this.locationName = location.getLocationName();
        this.x = location.getX();
        this.y = location.getY();
    }
}
