package com.example.chillisauce.reservations.service;

import com.example.chillisauce.reservations.dto.request.ReservationAttendee;
import com.example.chillisauce.reservations.dto.request.ReservationRequest;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.dto.response.*;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.entity.ReservationUser;
import com.example.chillisauce.reservations.exception.ReservationErrorCode;
import com.example.chillisauce.reservations.exception.ReservationException;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.repository.ReservationUserRepository;
import com.example.chillisauce.reservations.vo.ReservationTimetable;
import com.example.chillisauce.reservations.vo.TimeUnit;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.repository.LocationRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    private final LocationRepository locationRepository;

    /**
     * 회사 전체 예약 조회
     */
    public ReservationListResponse getAllReservations(String companyName, Integer page, UserDetailsImpl userDetails) {
        if (!userDetails.getUser().getCompanies().getCompanyName()
                .equals(companyName)) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER);
        }

        // 페이지네이션
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<Reservation> all = reservationRepository.findAllByCompanyName(companyName, pageable);

        // 회의실이 null, 유저가 null : 삭제된 객체
        List<ReservationDetailResponse> responseList =
                all.stream().map(x -> {
                    Mr meetingRoom = x.getMeetingRoom();
                    Long mrId = meetingRoom == null ? 0 : meetingRoom.getId();
                    String mrName = meetingRoom == null ? "삭제된 회의실" : meetingRoom.getLocationName();
                    User user = x.getUser();
                    String username = user == null ? "삭제된 유저" : user.getUsername();
                    return new ReservationDetailResponse(x.getId(), mrId, mrName, username,
                            x.getStartTime(), x.getEndTime());
                }).toList();

        return new ReservationListResponse(responseList);
    }

    /**
     * 회의실의 예약 타임테이블 조회
     *
     * @param selDate       : 선택 날짜
     * @param meetingRoomId : 회의실 id
     * @param userDetails   : 유저 인증 정보
     */
    @Transactional(readOnly = true)
    public ReservationTimetableResponse getReservationTimetable(LocalDate selDate,
                                                                Long meetingRoomId,
                                                                UserDetailsImpl userDetails) {
        String companyName = userDetails.getUser().getCompanies().getCompanyName();
        Optional<Location> location = locationRepository.findByIdAndCompanyName(meetingRoomId, companyName);

        if (location.isEmpty()) {
            throw new ReservationException(ReservationErrorCode.MEETING_ROOM_NOT_FOUND);
        }

        if (!location.get().isMr()) {
            throw new ReservationException(ReservationErrorCode.LOCATION_NOT_MEETING_ROOM);
        }

        Mr meetingRoom = (Mr) location.get();

        // 회의실의 해당 날짜에 해당하는 모든 예약 리스트
        List<Reservation> all = reservationRepository
                .findAllByMeetingRoomIdAndStartTimeBetween(meetingRoom.getId(),
                        selDate.atStartOfDay(), selDate.atTime(LocalTime.MAX));

        // 당일 예약 수 x timeSet entry 만큼 loop
        List<ReservationTimeResponse> timeList =
                ReservationTimetable.TIME_SET.stream().map(x -> getTimeUnitInfo(selDate, x, all))
                        // 07시부터 22시까지 정렬하기 위한 Comparator 구현
                        .sorted(((o1, o2) -> o1.getStart().isAfter(o2.getStart()) ? 1 :
                                o1.getStart().isBefore(o2.getStart()) ? -1 : 0))
                        .toList();

        return new ReservationTimetableResponse(meetingRoom.getId(), meetingRoom.getLocationName(), timeList);
    }

    // TIME_SET 시간 단위의 예약 가능 정보, 시각을 얻는 메서드
    ReservationTimeResponse getTimeUnitInfo(LocalDate selDate, TimeUnit timeUnit, List<Reservation> all) {
        LocalTime startTime = LocalTime.of(timeUnit.getStart().getHour(), timeUnit.getStart().getMinute());
        LocalTime endTime = LocalTime.of(timeUnit.getEnd().getHour(), timeUnit.getEnd().getMinute());
        LocalDateTime startDateTime = LocalDateTime.of(selDate, startTime);
        return new ReservationTimeResponse(isImpossible(startDateTime, all), startTime, endTime);
    }

    // 예약의 시작시간에 해당하는 타임은 true 반환
    boolean isImpossible(LocalDateTime time, List<Reservation> all) {
        // 오늘 이전 시각은 항상 예약 불가
        if (time.toLocalDate().isBefore(LocalDate.now())) {
            return true;
        }

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
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public ReservationResponse addReservation(Long meetingRoomId,
                                              ReservationRequest request,
                                              UserDetailsImpl userDetails) {
        User organizer = userDetails.getUser();
        String companyName = organizer.getCompanies().getCompanyName();
        Optional<Location> location = locationRepository.findByIdAndCompanyName(meetingRoomId, companyName);

        if (location.isEmpty()) {
            throw new ReservationException(ReservationErrorCode.MEETING_ROOM_NOT_FOUND);
        }

        if (!location.get().isMr()) {
            throw new ReservationException(ReservationErrorCode.LOCATION_NOT_MEETING_ROOM);
        }

        Mr meetingRoom = (Mr) location.get();

        List<LocalDateTime> list = request.getStartList().stream().map(ReservationTime::getStart)
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

        // 회의 참석자 유저리스트가 비어있으면 리턴
        if (request.getUserList() == null) {
            return new ReservationResponse(reservation);
        }

        // userList id -> User mapping
        List<Long> ids = request.getUserList().stream().mapToLong(ReservationAttendee::getUserId).boxed().toList();
        List<User> attendee = userRepository.findAllByIdInAndCompanies_CompanyName(ids, organizer.getCompanies().getCompanyName());

        // 참석자 리스트와 예약 정보를 ReservationUser 연결 테이블에 저장
        List<ReservationUser> info = attendee.stream().map(x -> new ReservationUser(x, reservation)).toList();
        reservationUserRepository.saveAll(info);

        // 모든 참석자의 스케줄에 회의 일정 추가
        List<Schedule> schedules = info.stream().map(x -> new Schedule(x.getReservation(), x.getAttendee())).toList();
        scheduleRepository.saveAll(schedules);
        return new ReservationResponse(reservation, attendee.stream()
                .map(x -> new UsernameResponse(x.getUsername())).toList());
    }

    /**
     * 예약 수정 - 시간 변경
     */
    @Transactional
    public ReservationResponse editReservation(Long reservationId,
                                               ReservationRequest request,
                                               UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ReservationException(ReservationErrorCode.INVALID_USER_RESERVATION_UPDATE);
        }

        List<LocalDateTime> list = request.getStartList().stream().map(ReservationTime::getStart)
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

        return new ReservationResponse(reservation);
    }

    /**
     * 예약 삭제
     */
    @Transactional
    public String deleteReservation(Long reservationId, UserDetailsImpl userDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        User user = userDetails.getUser();
        if (!reservation.getUser().getId().equals(user.getId())) {
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
