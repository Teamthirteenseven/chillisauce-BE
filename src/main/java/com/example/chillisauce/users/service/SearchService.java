package com.example.chillisauce.users.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.dto.response.UserDetailResponseDto;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
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
