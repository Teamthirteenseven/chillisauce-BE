package com.example.chillisauce.performance.dto;

import com.example.chillisauce.schedules.dto.ScheduleTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleInjectRequest {
    // 생성할 일정 수
    Integer days;
}
