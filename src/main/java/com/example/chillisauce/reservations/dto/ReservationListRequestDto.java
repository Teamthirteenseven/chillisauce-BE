package com.example.chillisauce.reservations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationListRequestDto {
    @NotEmpty(message = "요청의 시각 목록이 비어있습니다.")
    List<ReservationRequestDto> startList;
}
