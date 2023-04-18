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

    private List<BoxResponseDto> boxlist = new ArrayList<>();

    private List<MrResponseDto> mrlist = new ArrayList<>();

    private List<MultiBoxResponseDto> multiboxlist = new ArrayList<>();

    public SpaceResponseDto(Space space) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.boxlist = space.getBoxes().stream().map(BoxResponseDto::new).collect(Collectors.toList());
        this.mrlist = space.getMrs().stream().map(MrResponseDto::new).collect(Collectors.toList());
        this.multiboxlist = space.getMultiBoxes().stream().map(MultiBoxResponseDto::new).collect(Collectors.toList());
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
        this.boxlist = space.getBoxes().stream().map(BoxResponseDto::new).collect(Collectors.toList());
        this.mrlist = space.getMrs().stream().map(MrResponseDto::new).collect(Collectors.toList());
        this.multiboxlist = space.getMultiBoxes().stream().map(MultiBoxResponseDto::new).collect(Collectors.toList());
    }

    public SpaceResponseDto(Space space, Floor floor) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
        this.boxlist = space.getBoxes().stream().map(BoxResponseDto::new).collect(Collectors.toList());
        this.mrlist = space.getMrs().stream().map(MrResponseDto::new).collect(Collectors.toList());
        this.multiboxlist = space.getMultiBoxes().stream().map(MultiBoxResponseDto::new).collect(Collectors.toList());
    }

}