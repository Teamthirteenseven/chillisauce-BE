package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.request.ReservationRequestDto;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.dto.request.ReservationAttendee;
import com.example.chillisauce.reservations.dto.response.*;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.entity.ReservationUser;
import com.example.chillisauce.reservations.exception.ReservationErrorCode;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.repository.ReservationUserRepository;
import com.example.chillisauce.reservations.vo.ReservationTimetable;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationUserRepository reservationUserRepository;
    private final ScheduleRepository scheduleRepository;
    private final MrRepository meetingRoomRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

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

        return new ReservationTimetableResponseDto(meetingRoom.getId(), meetingRoom.getLocationName(), timeList);
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

        User organizer = userDetails.getUser(); // 회의 주최자

        List<LocalDateTime> list = requestDto.getStartList().stream().map(ReservationTime::getStart)
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
                .user(organizer)
                .meetingRoom(meetingRoom)
                .startTime(start)
                .endTime(end)
                .build();

        reservationRepository.save(reservation);

        //TODO: valid 추가
        if (requestDto.getUserList() == null) {
            return new ReservationResponseDto(reservation);
        }

        // userList id -> User mapping
        List<Long> ids = requestDto.getUserList().stream().mapToLong(ReservationAttendee::getUserId).boxed().toList();
        List<User> attendee = userRepository.findAllByIdInAndCompanies_CompanyName(ids, organizer.getCompanies().getCompanyName());

        // 참석자 리스트와 예약 정보를 ReservationUser 연결 테이블에 저장
        List<ReservationUser> info = attendee.stream().map(x -> new ReservationUser(x, reservation)).toList();
        reservationUserRepository.saveAll(info);

        // 모든 참석자의 스케줄에 회의 일정 추가
        List<Schedule> schedules = info.stream().map(x->new Schedule(x.getReservation(), x.getAttendee())).toList();
        scheduleRepository.saveAll(schedules);
        return new ReservationResponseDto(reservation, attendee.stream()
                .map(x -> new UsernameResponseDto(x.getUsername())).toList());
    }

    /**
     * 예약 수정 - 시간 변경
     */
    @Transactional
    public ReservationResponseDto editReservation(Long reservationId,
                                                  ReservationRequestDto requestDto,
                                                  UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER_RESERVATION_UPDATE);
        }

        List<LocalDateTime> list = requestDto.getStartList().stream().map(ReservationTime::getStart)
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

        return "success";
    }
}
