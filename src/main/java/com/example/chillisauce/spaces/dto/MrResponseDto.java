package com.example.chillisauce.spaces.dto;

import com.example.chillisauce.reservations.dto.response.ReservationResponseDto;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.UserLocation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MrResponseDto {
    private Long mrId;
    private String mrName;
    private String x;

    private String y;

    private List<ReservationResponseDto> reservationList;

    public MrResponseDto(Mr mr) {
        this.mrId = mr.getId();
        this.mrName = mr.getLocationName();
        this.x = mr.getX();
        this.y = mr.getY();
        this.reservationList =mr.getReservations().stream().map(ReservationResponseDto::new).collect(Collectors.toList());
    }

    public MrResponseDto(Long id, String mrName, String x, String y) {
        this.mrId = id;
        this.mrName = mrName;
        this.x = x;
        this.y = y;
    }

}

