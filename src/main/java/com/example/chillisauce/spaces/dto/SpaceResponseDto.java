package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.users.entity.Companies;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SpaceResponseDto {

    private Long spaceId;

    private String spaceName;

    private Long floorId;
    private String floorName;

    private final List<BoxResponseDto> boxlist = new ArrayList<>();

    private final List<MrResponseDto> mrlist = new ArrayList<>();

    private final List<MultiBoxResponseDto> multiboxlist = new ArrayList<>();

    public SpaceResponseDto(Space space) {
        if (space.getFloor() != null) {
            this.floorId = space.getFloor().getId();
        } else {
            this.floorId = null; //Space 선택조회시 에러 발생 이슈
        }

        if (space.getFloor() != null) {
            this.floorName = space.getFloor().getFloorName();
        } else {
            this.floorName = null;
        }
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        for (Box box : space.getBoxes()) {
            boxlist.add(new BoxResponseDto(box));
        }
        for (Mr mr : space.getMrs()) {
            mrlist.add(new MrResponseDto(mr));
        }
        for (MultiBox multiBox : space.getMultiboxes()) {
            multiboxlist.add(new MultiBoxResponseDto(multiBox));
        }
    }


    public SpaceResponseDto(Long id, String spaceName) {
        this.spaceId = id;
        this.spaceName = spaceName;

    }

}