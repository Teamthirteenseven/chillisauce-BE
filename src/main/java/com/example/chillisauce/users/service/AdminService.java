package com.example.chillisauce.users.service;

import com.example.chillisauce.security.UserDetailsImpl;
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
        List<User> allList = userRepository.findAllByCompanies_CompanyName(user.getCompanies().getCompanyName());
        return new UserListResponseDto(allList.stream().map(UserDetailResponseDto::new).toList());
    }

    /* 사원 선택 조회 */
    @Transactional(readOnly = true)
    public UserDetailResponseDto getUsers(Long userId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
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
        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }
        User getUser = userRepository.findByIdAndCompanies_CompanyName(userId, user.getCompanies().getCompanyName()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (requestDto.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new UserException(UserErrorCode.UNABLE_MODIFY_PERMISSION_FOR_ADMIN);
        }

        getUser.update(requestDto);
        userRepository.save(getUser);
        return new UserDetailResponseDto(getUser);
    }

}
