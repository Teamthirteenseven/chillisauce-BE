package com.example.chillisauce.schedules.dto;

import com.example.chillisauce.schedules.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponseDto {
    Long scId;
    String scTitle;
    String scComment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime scStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime scEnd;

    public ScheduleResponseDto(Schedule schedules) {
        this.scId = schedules.getId();
        this.scTitle = schedules.getTitle();
        this.scStart = schedules.getStartTime();
        this.scEnd = schedules.getEndTime();
        this.scComment = schedules.getComment();
    }
}
