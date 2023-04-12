package com.example.chillisauce.schedules.service;

import com.example.chillisauce.schedules.dto.ScheduleListResponseDto;
import com.example.chillisauce.schedules.dto.ScheduleRequestDto;
import com.example.chillisauce.schedules.dto.ScheduleResponseDto;
import com.example.chillisauce.schedules.entity.Schedules;
import com.example.chillisauce.schedules.exception.ScheduleErrorCode;
import com.example.chillisauce.schedules.exception.ScheduleException;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleListResponseDto getSchedules(LocalDate selDate, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<Schedules> schedules = scheduleRepository.findAllByStartTime(selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX));

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
        Schedules schedule = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        User user = userDetails.getUser();

        if(!user.getId().equals(schedule.getUser().getId())) {
            throw new ScheduleException(ScheduleErrorCode.DUPLICATED_TIME);
        }

        List<Schedules> duplicated = scheduleRepository
                .findAllByIdNotAndStartTimeLessThanAndEndTimeGreaterThan(scheduleId,
                        requestDto.getScStart(), requestDto.getScEnd());

        if(duplicated.size()!=0) {
            throw new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND);
        }

        schedule.update(requestDto);

        return null;
    }

    public String deleteSchedule(Long scheduleId, UserDetailsImpl userDetails) {
        Schedules schedule = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        User user = userDetails.getUser();

        if(!user.getId().equals(schedule.getUser().getId())) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_USER_RESERVATION_DELETE);
        }

        scheduleRepository.deleteById(schedule.getId());

        return "";
    }
}
