package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.spaces.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        this.boxList = space.getLocations().stream().filter(x -> x instanceof Box).map(x -> new BoxResponseDto((Box) x)).toList();
        this.mrList = space.getLocations().stream().filter(x -> x instanceof Mr).map(x -> new MrResponseDto((Mr) x)).toList();
        this.multiBoxList = space.getLocations().stream().filter(x -> x instanceof MultiBox).map(x -> new MultiBoxResponseDto((MultiBox) x)).toList();
    }
    public SpaceResponseDto(Space space, Long floorId, String floorName, List<Object[]> locationsWithUserLocations) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        this.floorId = floorId;
        this.floorName = floorName;

        this.boxList = locationsWithUserLocations.stream().filter(obj -> obj[0] instanceof Box).map(obj -> {
                    Box box = (Box) obj[0];
                    @SuppressWarnings("unchecked")
                    List<UserLocation> userLocations = (List<UserLocation>) obj[1];
                    UserLocation userLocation = userLocations != null ? userLocations.stream().findFirst().orElse(null) : null;
                    return new BoxResponseDto(box, userLocation);
                })
                .collect(Collectors.toList());


        this.mrList = space.getLocations().stream().filter(x -> x instanceof Mr).map(x -> new MrResponseDto((Mr) x)).toList();

        this.multiBoxList = locationsWithUserLocations.stream().filter(obj -> obj[0] instanceof MultiBox).map(obj -> {
                    MultiBox multiBox = (MultiBox) obj[0];
                    @SuppressWarnings("unchecked")
                    List<UserLocation> userLocations = (List<UserLocation>) obj[1];
                    return new MultiBoxResponseDto(multiBox, userLocations != null ? userLocations : Collections.emptyList());
                })
                .collect(Collectors.toList());
    }


        this.mrList = space.getLocations().stream().filter(x -> x instanceof Mr).map(x -> new MrResponseDto((Mr) x)).toList();

        this.multiBoxList = locationsWithUserLocations.stream().filter(obj -> obj[0] instanceof MultiBox)
                .map(obj -> {MultiBox multiBox = (MultiBox) obj[0]; //multiBoxResponseDto 으로 변환 캐스팅
                    UserLocation userLocation = (UserLocation) obj[1]; //userLocation 으로 변환 캐스팅
                    return new MultiBoxResponseDto(multiBox, userLocation); //객체들 수집
                })
                .collect(Collectors.toList());
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


}