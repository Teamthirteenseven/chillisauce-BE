package com.example.chillisauce.schedules.dto;

import com.example.chillisauce.schedules.entity.Schedules;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
    Long scId;
    String scTitle;
    LocalDateTime scStart;
    LocalDateTime scEnd;
    String scComment;

    public ScheduleResponseDto(Schedules schedules) {
        this.scId = schedules.getId();
        this.scTitle = schedules.getTitle();
        this.scStart = schedules.getStartTime();
        this.scEnd = schedules.getEndTime();
        this.scComment = schedules.getComment();
    }
}
