package com.example.chillisauce.global.security.jwt;

import com.example.chillisauce.global.security.UserDetailsServiceImpl;

import com.example.chillisauce.domain.users.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsServiceImpl userDetailsService;
    private final Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public JwtUtil(@Value("${jwt.secret.key}") final String secretKey, UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /*  header 토큰 가져오기 */
    public String getHeaderToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        {
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX))
                return bearerToken.substring(7);
        }
        return null;
    }

    /* 토큰 생성 */
    public String createToken(User user) {

        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(user.getEmail())
                        .claim("userId", user.getId())
                        .claim("role", user.getRole())
                        .claim("username", user.getUsername())
                        .claim("companyName", user.getCompanies().getCompanyName())
                        .setExpiration(new Date(date.getTime() + getAccessTime()))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    /* 토큰 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }


    /* 토큰 정보 추출 */
    @Transactional
    public String getUserInfoFromToken(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /* 스프링 시큐리티 인증객체 생성 */
    @Transactional
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /* 토큰 만료 시간 */
    public long getAccessTime() {
        return 8 * 60 * 60 * 1000L; // 8시간
    }

}