package com.example.chillisauce.users.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.dto.response.UserDetailResponseDto;
import com.example.chillisauce.users.entity.User;
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
        log.info("search user");
        User finder = userDetails.getUser();
        String companyName = finder.getCompanies().getCompanyName();
        List<User> users = userRepository.findAllByUsernameContainingAndCompanies(name);
        return users.stream().map(UserDetailResponseDto::new).toList();
    }
}
