package com.example.chillisauce.domain.users.service;

import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.users.dto.response.UserDetailResponseDto;
import com.example.chillisauce.domain.users.entity.User;
import com.example.chillisauce.domain.users.exception.UserErrorCode;
import com.example.chillisauce.domain.users.exception.UserException;
import com.example.chillisauce.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final UserRepository userRepository;

    public List<UserDetailResponseDto> searchUser(String name, UserDetailsImpl userDetails) {
        User finder = userDetails.getUser();
        String companyName = finder.getCompanies().getCompanyName();
        List<User> users = userRepository.findAllByUsernameContainingAndCompanies(name, companyName);

        if (users.isEmpty()) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        return users.stream().map(UserDetailResponseDto::new).toList();
    }
}
