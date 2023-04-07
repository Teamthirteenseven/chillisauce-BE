package com.example.chillisauce.schedules.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleRequestDto {
    String scTitle;
    LocalDateTime scStart;
    LocalDateTime scEnd;
    String scComment;

}
