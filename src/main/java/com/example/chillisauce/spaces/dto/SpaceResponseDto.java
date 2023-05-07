package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
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


    @Builder
    public SpaceResponseDto(Space space, Long floorId, String floorName, List<BoxResponseDto> boxList, List<MrResponseDto> mrList, List<MultiBoxResponseDto> multiBoxList) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floorId;
        this.floorName = floorName;
        this.boxList = boxList;
        this.mrList = mrList;
        this.multiBoxList = multiBoxList;

    }



}

