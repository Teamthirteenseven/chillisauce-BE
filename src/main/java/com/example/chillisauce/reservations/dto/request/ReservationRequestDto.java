package com.example.chillisauce.reservations.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {
    @NotEmpty(message = "요청의 시각 목록이 비어있습니다.")
    List<ReservationTime> startList;
    List<ReservationAttendee> userList;
}
