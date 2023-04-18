package com.example.chillisauce.schedules.service;

import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.exception.ReservationErrorCode;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.vo.TimeUnit;
import com.example.chillisauce.schedules.dto.*;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.exception.ScheduleErrorCode;
import com.example.chillisauce.schedules.exception.ScheduleException;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    // 예약 가능 첫 시각 : 오전 7시, 마지막 시각 : 22시
    private static final Integer OPEN_HOUR = 7;
    private static final Integer CLOSE_HOUR = 22;

    private final Set<TimeUnit> timeSet;

    /**
     * timeSet 초기화
     */
    @PostConstruct
    public void initializeTimeSet() {
        IntStream.range(OPEN_HOUR, CLOSE_HOUR + 1).forEach(x -> {
            LocalTime start = LocalTime.of(x, 0);
            LocalTime end = LocalTime.of(x, 59);
            timeSet.add(new TimeUnit(start, end));
        });
    }

    public ScheduleTimetableResponseDto getDaySchedules(LocalDate selDate, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        // 해당 날짜에 해당하는 모든 스케줄 리스트
        List<Schedule> all = scheduleRepository
                .findAllByUserIdAndStartTimeBetween(user.getId(), selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX));

        //TODO: 예약 수 x timeSet entry 만큼 loop 돌기 때문에 성능이 좋지 않음
        List<ScheduleTimeResponseDto> timeList =
                timeSet.stream().map(
                        x -> {
                            LocalTime startTime = LocalTime.of(x.getStart().getHour(), x.getStart().getMinute());
                            LocalTime endTime = LocalTime.of(x.getEnd().getHour(), x.getEnd().getMinute());
                            LocalDateTime startDateTime = LocalDateTime.of(selDate, startTime);
                            return new ScheduleTimeResponseDto(isOccupied(startDateTime, all), startTime, endTime);
                        }).toList();

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

    public ScheduleListResponseDto getAllSchedules(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<Schedule> schedules = scheduleRepository
                .findAllByUserId(user.getId());

        List<ScheduleResponseDto> dtoList = schedules.stream().map(ScheduleResponseDto::new).toList();

        return new ScheduleListResponseDto(dtoList);
    }

    public ScheduleResponseDto addSchedule(ScheduleRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<LocalDateTime> list = requestDto.getStartList().stream().map(ScheduleTime::getStart)
                .sorted().toList();
        LocalDateTime start = list.get(0);
        LocalDateTime end = list.get(list.size()-1).plusMinutes(59);

        // 시간이 겹치는 스케줄이 있는 경우 등록할 수 없음
        List<Schedule> duplicated = scheduleRepository
                .findFirstByUserIdAndStartTimeLessThanAndEndTimeGreaterThan(user.getId(), start, end);

        if(duplicated.size()!=0){
            throw new ScheduleException(ScheduleErrorCode.DUPLICATED_TIME);
        }

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

    public ScheduleResponseDto editSchedule(Long scheduleId,
                                            ScheduleRequestDto requestDto,
                                            UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        User user = userDetails.getUser();

        if(!user.getId().equals(schedule.getUser().getId())) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_USER_SCHEDULE_UPDATE);
        }

        List<LocalDateTime> list = requestDto.getStartList().stream().map(ScheduleTime::getStart)
                .sorted().toList();
        LocalDateTime start = list.get(0);
        LocalDateTime end = list.get(list.size()-1).plusMinutes(59);

        List<Schedule> duplicated = scheduleRepository
                .findAllByIdNotAndStartTimeLessThanAndEndTimeGreaterThan(scheduleId,
                        start, end);

        if(duplicated.size()!=0) {
            throw new ScheduleException(ScheduleErrorCode.DUPLICATED_TIME);
        }

        schedule.update(requestDto, start, end);

        return new ScheduleResponseDto(schedule);
    }

    public String deleteSchedule(Long scheduleId, UserDetailsImpl userDetails) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        User user = userDetails.getUser();

        if(!user.getId().equals(schedule.getUser().getId())) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_USER_SCHEDULE_DELETE);
        }

        scheduleRepository.deleteById(schedule.getId());

        return "";
    }
}
