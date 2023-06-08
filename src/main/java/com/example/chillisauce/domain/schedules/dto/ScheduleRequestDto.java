package com.example.chillisauce.domain.schedules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequestDto {
    @NotNull
    String scTitle;
    @NotNull
    String scComment;
    @NotEmpty
    List<ScheduleTime> startList;
}

