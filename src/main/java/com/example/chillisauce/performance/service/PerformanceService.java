package com.example.chillisauce.performance.service;

import com.example.chillisauce.performance.dto.ReservationInjectRequest;
import com.example.chillisauce.performance.dto.ScheduleInjectRequest;
import com.example.chillisauce.performance.dto.SpaceInjectRequest;
import com.example.chillisauce.performance.dto.UserInjectRequest;
import com.example.chillisauce.security.UserDetailsImpl;
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

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerformanceService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public String injectUsers(UserInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        Companies company = companyRepository.save(Companies.builder()
                .certification("testCert")
                .companyName("testCompany")
                .build());
        Integer count = request.count();

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

        userRepository.saveAll(testUserList);
        return "success";
    }

    public String injectSpaces(SpaceInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        return "success";
    }

    public String injectReservations(ReservationInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        return "success";
    }

    public String injectSchedules(ScheduleInjectRequest request, UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole()!=UserRoleEnum.SUPERUSER){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        return "success";
    }
}
