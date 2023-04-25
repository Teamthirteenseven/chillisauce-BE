package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.reservations.dto.ReservationResponseDto;
import com.example.chillisauce.spaces.entity.Mr;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class MrResponseDto {
    private final Long mrId;
    private final String mrName;
    private String x;

    private String y;

//    private List<ReservationResponseDto> responseList = new ArrayList<>();

    public MrResponseDto(Mr mr) {
        this.mrId = mr.getId();
        this.mrName = mr.getLocationName();
        this.x = mr.getX();
        this.y = mr.getY();

    }


}

