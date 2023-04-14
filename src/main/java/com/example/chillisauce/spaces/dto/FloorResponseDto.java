package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Floor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class FloorResponseDto {

    private Long floorId;

    private String floorName;

    private List<SpaceResponseDto> spaceList = new ArrayList<>();

    public FloorResponseDto(Floor floor) {
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
        this.spaceList = floor.getSpaces().stream().map(space -> new SpaceResponseDto(space, floor)).collect(Collectors.toList());
    }

    public FloorResponseDto(Long id, String floorName) {
        this.floorId = id;
        this.floorName = floorName;
    }
}

