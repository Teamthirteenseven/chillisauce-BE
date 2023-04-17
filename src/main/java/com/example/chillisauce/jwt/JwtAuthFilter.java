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
        String accessToken = jwtUtil.getHeaderToken(request, "Access");
        String refreshToken = jwtUtil.getHeaderToken(request, "Refresh");

//        String accessToken = jwtUtil.getHeaderTokenAccess(request);
//        String refreshToken = jwtUtil.getHeaderTokenRefresh(request);

        if(accessToken != null) {
            if(!jwtUtil.validateToken(accessToken)){
                jwtExceptionHandler(response, "Token Error", HttpStatus.UNAUTHORIZED.value());
                return;
            }
            setAuthentication(jwtUtil.getUserInfoFromToken(accessToken));
        }
        //엑세스토큰 만료 && 리프레시토큰 존재
        else if (refreshToken != null) {
            //리프레시토큰 검증 && 리프레시토큰 DB에서 존재유무 확인
            boolean isRefreshToken = jwtUtil.refreshTokenValidation(refreshToken);
            //리프레시 토큰이 유효하고 DB와 비교했을 때 똑같다면
            if (isRefreshToken) {
                //리프레시토큰으로 정보 가져오기
                String loginemail = jwtUtil.getUserInfoFromToken(refreshToken);
                // 새로운 어세스 토큰 발급
                String newAccessToken = jwtUtil.createToken(loginemail, "Access").substring(7);

                // 헤더에 어세스 토큰 추가
                jwtUtil.setHeaderAccessToken(response, newAccessToken);

                log.info("loginemail={}",loginemail);
                log.info("Token={}",newAccessToken);
                // Security context에 인증 정보 넣기
                setAuthentication(jwtUtil.getUserInfoFromToken(newAccessToken));
            }
            //리프레시토큰 만료 || 리프레시토큰이 DB와 일치하지 않으면
            else {
                jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST.value());
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
        try {
            String json = new ObjectMapper().writeValueAsString(new ResponseMessage(msg, statusCode, ""));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}