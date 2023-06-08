package com.example.chillisauce.domain.schedules.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListResponseDto {
    List<ScheduleResponseDto> scList;
}
