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

    private List<BoxResponseDto> boxList = new ArrayList<>();
    private List<MrResponseDto> mrList = new ArrayList<>();
    private List<MultiBoxResponseDto> multiBoxList = new ArrayList<>();

    public SpaceResponseDto(Space space) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();

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
        this.boxList = space.getLocations().stream().filter(x -> x instanceof Box).map(x -> new BoxResponseDto((Box) x)).toList();
        this.mrList = space.getLocations().stream().filter(x -> x instanceof Mr).map(x -> new MrResponseDto((Mr) x)).toList();
        this.multiBoxList = space.getLocations().stream().filter(x -> x instanceof MultiBox).map(x -> new MultiBoxResponseDto((MultiBox) x)).toList();
    }

    public SpaceResponseDto(Space space, Floor floor) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
        this.boxList = space.getLocations().stream().filter(x -> x instanceof Box).map(x -> new BoxResponseDto((Box) x)).toList();
        this.mrList = space.getLocations().stream().filter(x -> x instanceof Mr).map(x -> new MrResponseDto((Mr) x)).toList();
        this.multiBoxList = space.getLocations().stream().filter(x -> x instanceof MultiBox).map(x -> new MultiBoxResponseDto((MultiBox) x)).toList();
    }

    public SpaceResponseDto(Space space, Floor floor, List<BoxResponseDto> boxList,
                            List<MrResponseDto> mrList, List<MultiBoxResponseDto> multiBoxList) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
        this.boxList = boxList;
        this.mrList = mrList;
        this.multiBoxList = multiBoxList;
    }

}