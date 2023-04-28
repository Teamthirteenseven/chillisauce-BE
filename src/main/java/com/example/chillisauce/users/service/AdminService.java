package com.example.chillisauce.users.service;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.schedules.entity.Schedule;
import com.example.chillisauce.schedules.repository.ScheduleRepository;
import com.example.chillisauce.schedules.service.ScheduleService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.UserLocation;
import com.example.chillisauce.spaces.repository.UserLocationRepository;
import com.example.chillisauce.users.dto.RoleDeptUpdateRequestDto;
import com.example.chillisauce.users.dto.UserDetailResponseDto;
import com.example.chillisauce.users.dto.UserListResponseDto;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final UserLocationRepository userLocationRepository;

    /* 사원 목록 전체 조회 */
    @Transactional(readOnly = true)
    public UserListResponseDto getAllUsers(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !user.getRole().equals(UserRoleEnum.MANAGER)) {    //어드민과 매니저 권한 동일하게
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        List<User> allList = userRepository.findAllByCompanies_CompanyName(user.getCompanies().getCompanyName());
        return new UserListResponseDto(allList.stream().map(UserDetailResponseDto::new).toList());
    }

    /* 사원 선택 조회 */
    @Transactional(readOnly = true)
    public UserDetailResponseDto getUsers(Long userId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !user.getRole().equals(UserRoleEnum.MANAGER)) {    //어드민과 매니저 권한 동일하게
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        User getUser = userRepository.findByIdAndCompanies_CompanyName(userId, user.getCompanies().getCompanyName()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return new UserDetailResponseDto(getUser);
    }

    /* 사원 권한 수정 */
    @Transactional
    public UserDetailResponseDto editUser(Long userId, UserDetailsImpl userDetails, RoleDeptUpdateRequestDto requestDto) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !user.getRole().equals(UserRoleEnum.MANAGER)) {
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        User getUser = userRepository.findByIdAndCompanies_CompanyName(userId, user.getCompanies().getCompanyName()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (requestDto.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.UNABLE_MODIFY_PERMISSION_FOR_ADMIN);
        }

        if (requestDto.isUpdateRole() && getUser.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.DO_NOT_CHANGED_PERMISSION);
        }

        getUser.update(requestDto);
        userRepository.save(getUser);
        return new UserDetailResponseDto(getUser);
    }

    /* 사원 삭제 */
    @Transactional
    public String deleteUser(Long userId,UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        //퇴사처리 하려는 사원 찾기
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        //사원의 스케줄 삭제
        List<Schedule> schedules = scheduleRepository.findAllByUserId(userId);
        scheduleRepository.deleteAll(schedules);

        //사원의 예약 삭제
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);
        reservationRepository.deleteAll(reservations);

        //사원의 로케이션 삭제
        Optional<UserLocation> userLocation = userLocationRepository.findByUserId(userId);
        userLocation.ifPresent(userLocationRepository::delete);

        //회원 삭제
        userRepository.delete(findUser);
        return "사원 삭제 성공";
    }

}
