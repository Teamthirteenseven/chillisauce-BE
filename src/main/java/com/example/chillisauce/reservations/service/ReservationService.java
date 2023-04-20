package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.*;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.exception.ReservationErrorCode;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.vo.ReservationTimetable;
import com.example.chillisauce.reservations.vo.TimeUnit;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MrRepository meetingRoomRepository;
    private final CompanyRepository companyRepository;

    /**
     * 회사 전체 예약 조회
     */
    public ReservationListResponseDto getAllReservations(String companyName, UserDetailsImpl userDetails) {
        Companies companies = companyRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.COMPANY_NOT_FOUND));

        List<Reservation> all = reservationRepository.findAll();

        return new ReservationListResponseDto(all.stream().map(x -> {
            Mr meetingRoom = x.getMeetingRoom();
            User user = x.getUser();
            Long mrId = meetingRoom == null ? 0 : meetingRoom.getId();
            String username = user == null ? "탈퇴한 유저" : user.getUsername();
            return new ReservationDetailResponseDto(x, mrId, username);
        }).toList());
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

        //TODO : 당일 예약 수 x timeSet entry 만큼 loop - 성능 개선 필요
        List<ReservationTimeResponseDto> timeList =
                ReservationTimetable.TIME_SET.stream().map(
                                x -> {
                                    LocalTime startTime = LocalTime.of(x.getStart().getHour(), x.getStart().getMinute());
                                    LocalTime endTime = LocalTime.of(x.getEnd().getHour(), x.getEnd().getMinute());
                                    LocalDateTime startDateTime = LocalDateTime.of(selDate, startTime);
                                    return new ReservationTimeResponseDto(isOccupied(startDateTime, all), startTime, endTime);
                                })
                        // 07시부터 22시까지 정렬하기 위한 Comparator 구현
                        .sorted(((o1, o2) -> o1.getStart().isAfter(o2.getStart()) ? 1 :
                                o1.getStart().isBefore(o2.getStart()) ? -1 : 0))
                        .toList();

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
                                                 ReservationListRequestDto requestDto,
                                                 UserDetailsImpl userDetails) {
        Mr meetingRoom = meetingRoomRepository.findById(meetingRoomId).orElseThrow(
                () -> new ReservationException(ReservationErrorCode.MEETING_ROOM_NOT_FOUND));

        User user = userDetails.getUser();

        List<LocalDateTime> list = requestDto.getStartList().stream().map(ReservationRequestDto::getStart)
                .sorted().toList();
        LocalDateTime start = list.get(0);
        LocalDateTime end = list.get(list.size() - 1).plusMinutes(59);

        // 시간이 겹치는 예약은 할 수 없음
        reservationRepository
                .findFirstByMeetingRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        meetingRoom.getId(), start, end)
                .ifPresent(x -> {
                    throw new ReservationException(ReservationErrorCode.DUPLICATED_TIME);
                });

        Reservation reservation = Reservation.builder()
                .user(user)
                .meetingRoom(meetingRoom)
                .startTime(start)
                .endTime(end)
                .build();

        reservationRepository.save(reservation);
        return new ReservationResponseDto(reservation);
    }

    /**
     * 예약 수정 - 시간 변경
     */
    @Transactional
    public ReservationResponseDto editReservation(Long reservationId,
                                                  ReservationListRequestDto requestDto,
                                                  UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().getEmail().equals(user.getEmail())) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER_RESERVATION_UPDATE);
        }

        List<LocalDateTime> list = requestDto.getStartList().stream().map(ReservationRequestDto::getStart)
                .sorted().toList();
        LocalDateTime start = list.get(0);
        LocalDateTime end = list.get(list.size() - 1).plusMinutes(59);

        // 수정요청에 해당하는 시각에 예약이 없는지 검증
        // 수정 대상 예약은 제외하고 검증해야함
        List<Reservation> duplicatedReservations = reservationRepository
                .findAllByMeetingRoomIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        reservation.getMeetingRoom().getId(),
                        reservationId,
                        start,
                        end);

        if (!duplicatedReservations.isEmpty()) {
            throw new ReservationException(ReservationErrorCode.DUPLICATED_TIME);
        }

        reservation.update(start, end);

        return new ReservationResponseDto(reservation);
    }

    @Transactional
    public String deleteReservation(Long reservationId, UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().getEmail().equals(user.getEmail())) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER_RESERVATION_UPDATE);
        }

        reservationRepository.deleteById(reservation.getId());

        return "success";
    }

    @Transactional
    public String deleteMeetingRoomInReservations(Long meetingRoomId, UserDetailsImpl userDetails) {
        List<Reservation> all = reservationRepository.findAllByMeetingRoomId(meetingRoomId);
        for (Reservation reservation : all) {
            reservation.update(null);
        }

        return null;
    }
}
