package com.example.chillisauce.actuator;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.security.SuperuserInformation;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class SuperuserService {
    private final JwtUtil jwtUtil;
    private final SuperuserInformation superuserInformation;
    public String loginSuperuser(SuperuserRequest request, HttpServletResponse response){

//        if(!request.password.equals(superuserInformation.getSuperUser().getPassword()) ||
//                !request.username.equals(superuserInformation.getSuperUser().getEmail())){
//            throw new UserException(UserErrorCode.NOT_HAVE_PERMISSION);
//        }

        String token = jwtUtil.createSuperuserToken(request.username, "Access");
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return "success";
    }
}
