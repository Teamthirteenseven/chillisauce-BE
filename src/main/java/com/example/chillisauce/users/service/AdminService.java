package com.example.chillisauce.users.service;

import com.example.chillisauce.security.UserDetailsImpl;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    /* 사원 목록 전체 조회 */
    @Transactional(readOnly = true)
    public UserListResponseDto getAllUsers(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        List<User> allList = userRepository.findAll();
        return new UserListResponseDto(allList.stream().map(UserDetailResponseDto::new).toList());
    }

    /* 사원 선택 조회 */
    @Transactional(readOnly = true)
    public UserDetailResponseDto getUsers(Long userId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        User getUser = userRepository.findById(userId).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return new UserDetailResponseDto(getUser);
    }

    /* 사원 권한 수정 */
//    @Transactional
//    public UserDetailResponseDto editUser(Long userId, UserDetailsImpl userDetails, String role) {
//        User user = userDetails.getUser();
//        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
//            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
//        }
//        User getUser = userRepository.findById(userId).orElseThrow(
//                () -> new UserException(UserErrorCode.USER_NOT_FOUND));
//        getUser.update(role);
//        userRepository.save(getUser);
//        return new UserDetailResponseDto(getUser);
//    }
}
