package com.example.chillisauce.schedules.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTime {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime start;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LocalDateTime time) {
            return start.isEqual(time);
        }
        return false;
    }
}
