package com.example.chillisauce.security;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // jwt토큰 발급절차에서 subject의 내용이 달라질 경우 이 로직에서도 수정이 필요
    @Override
    /* 테스트1. 유저 인증객체인 UserDetailsImpl을 캐싱한다. */
    @Cacheable(cacheNames = {"UserDetails"}, key = "#email")
    /* 테스트1. 유저 인증객체인 UserDetailsImpl을 캐싱한다. */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        log.info("@@@@@@@@@@@유저 인증객체 생성={}", user);
        return new UserDetailsImpl(user, user.getUsername());
    }

}