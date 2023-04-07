package com.example.chillisauce.schedules.service;

import com.example.chillisauce.schedules.dto.ScheduleListResponseDto;
import com.example.chillisauce.schedules.dto.ScheduleRequestDto;
import com.example.chillisauce.schedules.dto.ScheduleResponseDto;
import com.example.chillisauce.schedules.entity.Schedules;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleListResponseDto getSchedules(LocalDate selDate, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<Schedules> schedules = scheduleRepository.findAllByStartTime(selDate.atStartOfDay());

        List<ScheduleResponseDto> dtoList = schedules.stream().map(ScheduleResponseDto::new).toList();

        return new ScheduleListResponseDto(dtoList);
    }

    public ScheduleResponseDto addSchedule(ScheduleRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        Schedules schedules = new Schedules(requestDto, user);

        Schedules saved = scheduleRepository.save(schedules);

        return new ScheduleResponseDto(saved);
    }

    public ScheduleResponseDto editSchedule(Long scheduleId,
                                            ScheduleRequestDto requestDto,
                                            UserDetailsImpl userDetails) {
        return null;
    }

    public String deleteSchedule(Long scheduleId, UserDetailsImpl userDetails) {

        return "";
    }
}
