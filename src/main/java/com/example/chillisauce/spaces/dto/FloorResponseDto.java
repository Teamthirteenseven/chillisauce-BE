package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FloorResponseDto {

    private Long floorId;

    private String floorName;

    private final List<SpaceResponseDto> spaceList = new ArrayList<>();

    public FloorResponseDto(Floor floor) {
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
        for (Space space : floor.getSpaces()){
            spaceList.add(new SpaceResponseDto(space));
        }
    }

}

