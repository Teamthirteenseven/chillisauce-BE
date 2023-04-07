package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.dto.ReservationResponseDto;
import com.example.chillisauce.reservations.dto.ReservationTimeResponseDto;
import com.example.chillisauce.reservations.dto.ReservationTimetableResponseDto;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.exception.ReservationErrorCode;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.vo.TimeUnit;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.MrRepository;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MrRepository meetingRoomRepository;
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

    /**
     * 타임테이블 조회
     */
    @Transactional(readOnly = true)
    public ReservationTimetableResponseDto getReservationTimetable(LocalDate selDate,
                                                                   Long meetingRoomId,
                                                                   UserDetailsImpl userDetails) {
        Mr meetingRoom = meetingRoomRepository.findById(meetingRoomId).orElseThrow(
                () -> new ReservationException(ReservationErrorCode.MEETING_ROOM_NOT_FOUND));

        User user = userDetails.getUser();

        // 회의실의 해당 날짜에 해당하는 모든 예약 리스트
        List<Reservation> all = reservationRepository
                .findAllByMeetingRoomIdAndStartTimeBetween(meetingRoom.getId(),
                        selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX));

        //TODO: 예약 수 x timeSet entry 만큼 loop 돌기 때문에 성능이 좋지 않음
        List<ReservationTimeResponseDto> timeList =
                timeSet.stream().map(
                        x -> {
                            LocalTime startTime = LocalTime.of(x.getStart().getHour(), x.getStart().getMinute());
                            LocalTime endTime = LocalTime.of(x.getEnd().getHour(), x.getEnd().getMinute());
                            LocalDateTime startDateTime = LocalDateTime.of(selDate, startTime);
                            return new ReservationTimeResponseDto(isOccupied(startDateTime, all), startTime, endTime);
                        }).toList();
        return new ReservationTimetableResponseDto(meetingRoom.getId(), timeList);
    }

    // 예약의 시작시간에 해당하는 타임은 true 반환
    boolean isOccupied(LocalDateTime time, List<Reservation> all) {
        for (Reservation reservation : all) {
            LocalDateTime start = reservation.getStartTime();
            LocalDateTime end = reservation.getEndTime();
            if (time.isEqual(start) || (time.isAfter(start) && time.isBefore(end))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 회의실 예약 등록
     */
    @Transactional
    public ReservationResponseDto addReservation(Long meetingRoomId,
                                                 ReservationRequestDto requestDto,
                                                 UserDetailsImpl userDetails) {
        Mr meetingRoom = meetingRoomRepository.findById(meetingRoomId).orElseThrow(
                () -> new ReservationException(ReservationErrorCode.MEETING_ROOM_NOT_FOUND));

        User user = userDetails.getUser();

        //FIXME: requestDto setter 사용해서 59 하드코딩으로 더함 - 나중에 end LocalDateTime 받아야함
        LocalDateTime endTime = LocalDateTime.of(requestDto.getStart().toLocalDate(), requestDto.getStart().toLocalTime());
        endTime = endTime.plusMinutes(59);
        requestDto.setEnd(endTime);

        //TODO: validateTime 에서 오늘 날짜 이전 것은 등록하지 못하게 수정(?)
        if (!validateTime(requestDto.getStart(), requestDto.getEnd())) {
            throw new ReservationException(ReservationErrorCode.NOT_PROPER_TIME);
        }

        // 시간이 겹치는 예약은 할 수 없음
        reservationRepository
                .findFirstByMeetingRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        meetingRoom.getId(), requestDto.getStart(), requestDto.getEnd())
                .ifPresent(x -> {
                    throw new ReservationException(ReservationErrorCode.DUPLICATED_TIME);
                });

        Reservation reservation = new Reservation(requestDto, user, meetingRoom);
        reservationRepository.save(reservation);
        return new ReservationResponseDto(reservation);
    }

    /**
     * 시간 유효성 검사
     * start 시각이 end 시각보다 작아야 한다.
     */
    private boolean validateTime(LocalDateTime start, LocalDateTime end) {
        return !start.isAfter(end);
    }

    /**
     * 예약 수정
     */
    @Transactional
    public ReservationResponseDto editReservation(Long reservationId,
                                                  ReservationRequestDto requestDto,
                                                  UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().getEmail().equals(user.getEmail())) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER_RESERVATION_UPDATE);
        }

        //FIXME: requestDto setter 사용해서 59 하드코딩으로 더함 - 나중에 end LocalDateTime 받아야함
        LocalDateTime endTime = LocalDateTime.of(requestDto.getStart().toLocalDate(), requestDto.getStart().toLocalTime());
        endTime = endTime.plusMinutes(59);
        requestDto.setEnd(endTime);

        // 수정요청에 해당하는 시각에 예약이 없는지 검증
        // 수정 대상 예약은 제외하고 검증해야함
        List<Reservation> duplicatedReservations = reservationRepository
                .findAllByMeetingRoomIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        reservation.getMeetingRoom().getId(),
                        reservationId,
                        requestDto.getStart(),
                        requestDto.getEnd());

        if (!duplicatedReservations.isEmpty()) {
            throw new ReservationException(ReservationErrorCode.DUPLICATED_TIME);
        }

        reservation.update(requestDto);

        return null;
    }

    @Transactional
    public String deleteReservation(Long reservationId, UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().equals(user)) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER_RESERVATION_UPDATE);
        }

        reservationRepository.deleteById(reservationId);

        return "";
    }
}
