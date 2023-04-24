package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.users.entity.Companies;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SpaceResponseDto {

    private Long spaceId;

    private String spaceName;

    private Long floorId;
    private String floorName;
    private List<LocationDto> locationList = new ArrayList<>();


    public SpaceResponseDto(Space space) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.locationList = space.getLocations().stream().map(LocationDto::new).collect(Collectors.toList());
    }



    public SpaceResponseDto(Long id, String spaceName) {
        this.spaceId = id;
        this.spaceName = spaceName;
    }

    public SpaceResponseDto(Space space, Long floorId, String floorName) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floorId;
        this.floorName = floorName;
        this.locationList = space.getLocations().stream().map(LocationDto::new).collect(Collectors.toList());

    }

    public SpaceResponseDto(Space space, Floor floor) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
        this.locationList = space.getLocations().stream().map(LocationDto::new).collect(Collectors.toList());

    }

}