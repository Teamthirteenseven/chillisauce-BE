package com.example.chillisauce.reservations.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.dto.UserDetailResponseDto;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserRepository userRepository;

    public List<UserDetailResponseDto> searchUser(String name, UserDetailsImpl userDetails) {
        User finder = userDetails.getUser();
        String companyName = finder.getCompanies().getCompanyName();
        List<User> users = userRepository.findAllByUsernameContainingAndCompanies_CompanyName(name, companyName);
        return users.stream().map(UserDetailResponseDto::new).toList();
    }
}
