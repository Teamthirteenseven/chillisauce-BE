package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
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

    private final List<BoxResponseDto> boxlist = new ArrayList<>();

    private final List<MrResponseDto> mrlist = new ArrayList<>();
    public SpaceResponseDto(Space space) {
        this.spaceId = space.getId();
        this.spaceName = space.getSpaceName();
        for (Box box : space.getBoxes()){
            boxlist.add(new BoxResponseDto(box));
        }
        for (Mr mr : space.getMrs()){
            mrlist.add(new MrResponseDto(mr));
        }
    }



    public SpaceResponseDto(Long id, String spaceName) {
        this.spaceId = id;
        this.spaceName = spaceName;
    }

}
