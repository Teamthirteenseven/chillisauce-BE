package com.example.chillisauce.domain.spaces.dto.response;

import com.example.chillisauce.domain.spaces.entity.Mr;
import com.example.chillisauce.domain.reservations.dto.response.ReservationResponse;
import lombok.*;

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

    private List<ReservationResponse> reservationList;

    public MrResponseDto(Mr mr) {
        this.mrId = mr.getId();
        this.mrName = mr.getLocationName();
        this.x = mr.getX();
        this.y = mr.getY();
        this.reservationList =mr.getReservations().stream().map(ReservationResponse::new).collect(Collectors.toList());
    }

    public MrResponseDto(Long id, String mrName, String x, String y) {
        this.mrId = id;
        this.mrName = mrName;
        this.x = x;
        this.y = y;
    }

}

