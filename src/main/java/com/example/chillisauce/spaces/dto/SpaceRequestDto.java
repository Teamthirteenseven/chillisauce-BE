package com.example.chillisauce.spaces.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SpaceRequestDto {
    private String spaceName;

    public SpaceRequestDto(String spaceName) {
        this.spaceName = spaceName;
    }
}
