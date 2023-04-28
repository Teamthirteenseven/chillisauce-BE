package com.example.chillisauce.actuator;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class SuperuserService {
    private final JwtUtil jwtUtil;

    @Value("${spring.security.user.name}")
    private String superusername;
    @Value("${spring.security.user.password}")
    private String superuserPassword;

    public String loginSuperuser(SuperuserRequest request, HttpServletResponse response){

        if(!request.password.equals(superuserPassword) || !request.username.equals(superusername)){
            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
        }

        String token = jwtUtil.createSuperuserToken(request.username, "Access");
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return "success";
    }
}
