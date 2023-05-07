package com.example.chillisauce.spaces.dto.response;

import com.example.chillisauce.spaces.entity.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter

public class SpaceListResponseDto {
    private Long spaceId;
    private String spaceName;
    private Long floorId;
    private String floorName;
    public SpaceListResponseDto(Long spaceId, String spaceName, Long floorId, String floorName) {
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.floorId = floorId;
        this.floorName = floorName;
    }


    public SpaceListResponseDto(Space space) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = space.getFloor() != null ? space.getFloor().getId() : null;
        this.floorName = space.getFloor() != null ? space.getFloor().getFloorName() : null;
    }
}
