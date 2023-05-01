package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.*;
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

    public SpaceResponseDto(Space space, Long floorId, String floorName) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floorId;
        this.floorName = floorName;
        this.boxList = space.getLocations().stream().filter(x -> x instanceof Box).map(x -> {
                    Box box = (Box) x;
                    UserLocation userLocation = box.getUserLocations().stream().findFirst().orElse(null);
                    return new BoxResponseDto(box, userLocation);
                }).toList();

        this.mrList = space.getLocations().stream().filter(x -> x instanceof Mr).map(x -> new MrResponseDto((Mr) x)).toList();
        this.multiBoxList = space.getLocations().stream().filter(x -> x instanceof MultiBox).map(x -> {
                    MultiBox multiBox = (MultiBox) x;
                    List<UserLocation> userLocations = multiBox.getUserLocations();
                    return new MultiBoxResponseDto(multiBox, userLocations);
                }).toList();
    }


    public SpaceResponseDto(Space space, Floor floor) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floor.getId();
        this.floorName = floor.getFloorName();
    }

}
