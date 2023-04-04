package com.example.chillisauce.spaces.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpaceRequestDto {
    private String spaceName;
    private List<BoxRequestDto> boxlist;
    private List<MrRequestDto> mrlist;

    public List<BoxRequestDto> getBoxlist() {
        if (boxlist == null) {
            boxlist = new ArrayList<>(); //null 값일 경우 빈ArrayList 반환해 통해 NullPointerException 을 방지
        }
        return boxlist;
    }

    public List<MrRequestDto> getMrlist() {
        if (mrlist == null) {
            mrlist = new ArrayList<>();
        }
        return mrlist;
    }
}
