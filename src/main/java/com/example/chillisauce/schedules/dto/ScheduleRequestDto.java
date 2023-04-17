package com.example.chillisauce.schedules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequestDto {
    @NotNull
    String scTitle;
    LocalDateTime scStart;
    LocalDateTime scEnd;
    String scComment;

}

