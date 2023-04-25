package com.example.chillisauce.jwt;

import com.example.chillisauce.message.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getHeaderTokenEx(request);
//        String refreshToken = jwtUtil.getHeaderToken(request, "Refresh");
        /* 수정1. 리프레시토큰을 쿠키로 전환*/
        String refreshToken = jwtUtil.getCookieToken(request, JwtUtil.REFRESH_TOKEN);


//        String checkedRefreshEmail = jwtUtil.getUserInfoFromToken(refreshToken);
        Long checkedExpired = jwtUtil.getRefreshTime();

//        log.info("리프레시토큰 이메일 ={}", checkedRefreshEmail);
        log.info("리프레시토큰의 만료시간 ={}", checkedExpired);
        log.info("리프레시토큰 있어요@@@@={}", refreshToken);
//        String accessToken = jwtUtil.getHeaderTokenAccess(request);
//        String refreshToken = jwtUtil.getHeaderTokenRefresh(request);

        if(accessToken != null) {
            if(!jwtUtil.validateToken(accessToken)){
                jwtExceptionHandler(response, "Token Error", HttpStatus.UNAUTHORIZED.value());
                return;
            }
            setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
        }
        else {    //리프레시토큰으로 API 요청을 보냈을 때, 커스텀에러설정 (메세지가 ??????로 뜬다.)
            if (refreshToken == null) {
                jwtExceptionHandler(response, "잘못된 요청입니다.", HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(email);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(new ResponseMessage(msg, statusCode, ""));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}