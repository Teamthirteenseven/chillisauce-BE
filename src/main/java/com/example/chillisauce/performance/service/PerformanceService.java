package com.example.chillisauce.performance.service;

import com.example.chillisauce.performance.dto.ReservationInjectRequest;
import com.example.chillisauce.performance.dto.ScheduleInjectRequest;
import com.example.chillisauce.performance.dto.SpaceInjectRequest;
import com.example.chillisauce.performance.dto.UserInjectRequest;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.schedules.vo.ScheduleTimeTable;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private final SpaceRepository spaceRepository;
    private final BoxRepository boxRepository;

    private final MultiBoxRepository multiBoxRepository;
    private final MrRepository mrRepository;
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;

    public String injectUsers(UserInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        Companies company = userDetails.getUser().getCompanies();
        companyRepository.saveAndFlush(company);
        Integer count = request.getCount();

        // request 카운트만큼 유저 생성
        List<User> testUserList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String username = i + "번 사용자";
            String email = "test" + i + "@test" + company.getId() + ".com";

            User user = User.builder()
                    .email(email)
                    .username(username)
                    .role(UserRoleEnum.USER)
                    .password("1234qwer!")
                    .companies(company)
                    .build();

            testUserList.add(user);
        }

        userRepository.saveAllAndFlush(testUserList);
        return "success";
    }

    public String injectSpaces(SpaceInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        Companies company = userDetails.getUser().getCompanies();
        Integer count = request.getCount();


        List<Space> spaceList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String spaceName = i + "번 공간";
            Space space = Space.builder()
                    .companies(company)
                    .spaceName(spaceName)
                    .build();
            spaceList.add(space);
        }
        spaceRepository.saveAll(spaceList);

        List<Box> boxList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String boxName = i + "번 박스";
            String x = i + "";
            String y = i + "";
            Box box = new Box(boxName, x , y ,spaceList.get(0));
            boxList.add(box);
        }
        boxRepository.saveAll(boxList);

        List<MultiBox> multiBoxList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String multiBoxName = i + "번 멀티박스";
            String x = i + "";
            String y = i + "";
            MultiBox multiBox = new MultiBox(multiBoxName, x, y, spaceList.get(0));
            multiBoxList.add(multiBox);
        }
        multiBoxRepository.saveAll(multiBoxList);

        List<Mr> meetingRoomList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String mrName = i + "번 회의실";
            String x = i + "";
            String y = i + "";
            Mr mr = new Mr(mrName, x, y, spaceList.get(0));
            meetingRoomList.add(mr);
        }
        mrRepository.saveAll(meetingRoomList);

        return "success";
    }


    public String injectReservations(ReservationInjectRequest request, Long mrId, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        // 데이터 개수 : 회의실 10개 * 풀타임(7~22) = 160개 데이터
        Integer count = request.getCount();
        User organizer = userDetails.getUser(); // 회의 주최자

        List<Mr> mrList = mrRepository.findAll().stream().filter(x->x.getId()>=21).toList();

        List<LocalDateTime> timeList = request.getStartList().stream().map(ReservationTime::getStart)
                .sorted().toList();

        List<Reservation> reservationList = new ArrayList<>();

        for (Mr mr : mrList) {
            for (LocalDateTime t : timeList) {
                Reservation reservation = Reservation.builder()
                        .user(organizer)
                        .meetingRoom(mr)
                        .startTime(t)
                        .endTime(t.plusMinutes(59))
                        .build();
                reservationList.add(reservation);
            }
        }
        reservationRepository.saveAll(reservationList);
        return "success";
    }

    public String injectSchedules(ScheduleInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        // 유저 10명, 1달(30일)치 1시간단위 스케줄 풀타임(7~22) = 10*30*16 = 4800개 데이터
        List<User> userList = userRepository.findAll().stream().filter(x-> x.getId()>1 && x.getId()<12).toList();
        List<Schedule> scheduleList = new ArrayList<>();

        for (User user : userList) {
            for (int i = 0; i < request.getDays(); i++) {
                LocalDate date = LocalDate.now();
                for (int j = ScheduleTimeTable.OPEN_HOUR; j <= ScheduleTimeTable.CLOSE_HOUR; j++) {
                    Schedule schedule = Schedule.builder()
                            .startTime(LocalDateTime.of(date, LocalTime.of(j, 0)))
                            .endTime(LocalDateTime.of(date, LocalTime.of(j, 59)))
                            .user(user)
                            .comment("test Schedule comment")
                            .title("test Schedule title")
                            .build();

                    scheduleList.add(schedule);
                }
            }
        }

        scheduleRepository.saveAll(scheduleList);

        return "success";
    }
}
