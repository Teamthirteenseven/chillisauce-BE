package com.example.chillisauce.schedules.service;

import com.example.chillisauce.reservations.vo.TimeUnit;
import com.example.chillisauce.schedules.dto.*;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.exception.ScheduleErrorCode;
import com.example.chillisauce.schedules.exception.ScheduleException;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.schedules.vo.ScheduleTimeTable;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public ScheduleTimetableResponseDto getDaySchedules(LocalDate selDate, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        // 해당 날짜에 해당하는 모든 스케줄 리스트
        List<Schedule> all = scheduleRepository
                .findAllByUserIdAndStartTimeBetween(user.getId(), selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX));

        // 예약 수 x timeSet entry lop
        List<ScheduleTimeResponseDto> timeList =
                ScheduleTimeTable.TIME_SET.stream().map(
                        x -> {
                            LocalTime startTime = LocalTime.of(x.getStart().getHour(), x.getStart().getMinute());
                            LocalTime endTime = LocalTime.of(x.getEnd().getHour(), x.getEnd().getMinute());
                            LocalDateTime startDateTime = LocalDateTime.of(selDate, startTime);
                            return new ScheduleTimeResponseDto(isOccupied(startDateTime, all), startTime, endTime);
                        })
                        // 07시부터 22시까지 정렬하기 위한 Comparator 구현
                        .sorted(((o1, o2) -> o1.getStart().isAfter(o2.getStart()) ? 1 :
                                o1.getStart().isBefore(o2.getStart()) ? -1 : 0))
                        .toList();

        return new ScheduleTimetableResponseDto(timeList);
    }

    // 예약의 시작시간에 해당하는 타임은 true 반환
    boolean isOccupied(LocalDateTime time, List<Schedule> all) {
        for (Schedule schedule : all) {
            LocalDateTime start = schedule.getStartTime();
            LocalDateTime end = schedule.getEndTime();
            if (time.isEqual(start) || (time.isAfter(start) && time.isBefore(end))) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public ScheduleListResponseDto getAllSchedules(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<Schedule> schedules = scheduleRepository
                .findAllByUserId(user.getId());

        List<ScheduleResponseDto> dtoList = schedules.stream().map(ScheduleResponseDto::new).toList();

        return new ScheduleListResponseDto(dtoList);
    }

    @Transactional
    public ScheduleResponseDto addSchedule(ScheduleRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        // startList 정렬
        List<LocalDateTime> list = requestDto.getStartList().stream().map(ScheduleTime::getStart)
                .sorted().toList();
        LocalDateTime start = list.get(0);
        LocalDateTime end = list.get(list.size()-1).plusMinutes(59);

        Schedule schedules = Schedule.builder()
                .user(user)
                .title(requestDto.getScTitle())
                .comment(requestDto.getScComment())
                .startTime(start)
                .endTime(end)
                .build();

        Schedule saved = scheduleRepository.save(schedules);

        return new ScheduleResponseDto(saved);
    }

    @Transactional
    public ScheduleResponseDto editSchedule(Long scheduleId,
                                            ScheduleRequestDto requestDto,
                                            UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        User user = userDetails.getUser();

        if(!user.getId().equals(schedule.getUser().getId())) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_USER_SCHEDULE_UPDATE);
        }

        // startList 정렬
        List<LocalDateTime> list = requestDto.getStartList().stream().map(ScheduleTime::getStart)
                .sorted().toList();
        LocalDateTime start = list.get(0);
        LocalDateTime end = list.get(list.size()-1).plusMinutes(59);

        schedule.update(requestDto, start, end);

        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    public String deleteSchedule(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        User user = userDetails.getUser();

        if(!user.getId().equals(schedule.getUser().getId())) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_USER_SCHEDULE_DELETE);
        }

        scheduleRepository.deleteById(schedule.getId());

        return "success";
    }
}
