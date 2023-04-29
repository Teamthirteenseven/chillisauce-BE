package com.example.chillisauce.security;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final SuperuserInformation superuserInformation;
    // jwt토큰 발급절차에서 subject의 내용이 달라질 경우 이 로직에서도 수정이 필요
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(email.equals(superuserInformation.getSuperUser().getEmail())){
            log.info("Prometheus scrape success");
            return new UserDetailsImpl(superuserInformation.getSuperUser(), null);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new UserDetailsImpl(user, user.getUsername());
    }

}