package com.example.chillisauce.spaces.dto.response;

import com.example.chillisauce.spaces.entity.Floor;
import lombok.Builder;
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

    private List<SpaceListResponseDto> spaceList = new ArrayList<>();
    @Builder
    public FloorResponseDto(Long floorId, String floorName, List<SpaceListResponseDto> spaceList) {
        this.floorId = floorId;
        this.floorName = floorName;
        this.spaceList = spaceList;
    }



    public FloorResponseDto(Floor floor) {
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
    }
}

