package com.example.chillisauce.performance.dto;

import com.example.chillisauce.reservations.dto.request.ReservationTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInjectRequest {
    // 생성 예약 수
    List<ReservationTime> startList;
    Integer count;
}
