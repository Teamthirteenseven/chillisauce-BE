package com.example.chillisauce.jwt;

import com.example.chillisauce.security.UserDetailsServiceImpl;
import com.example.chillisauce.users.dto.TokenDto;
import com.example.chillisauce.users.entity.RefreshToken;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.RefreshTokenRepository;
import com.example.chillisauce.users.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    public static final String ACCESS_TOKEN = "Access_Token";   //헤더에 명시할 이름 기존 Authorization 에서 변경
    public static final String REFRESH_TOKEN = "Refresh_Token"; //엑세스토큰과 같이 리프레시토큰을 보내기 때문에 헤더에 같이 추가됨.
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsServiceImpl userDetailsService;       //스프링 시큐리티 의존성 주입
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.secret.key}") //절대 보여주지마...!
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;



    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰 가져오기
    public String getHeaderToken(HttpServletRequest request, String type) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); {
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX))
                return bearerToken.substring(7);
        }
        return type.equals("Access") ? request.getHeader(AUTHORIZATION_HEADER) :request.getHeader(REFRESH_TOKEN);
    }

//    public String getHeaderTokenAccess(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); {
//            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX))
//                return bearerToken.substring(7);
//        }
//        return request.getHeader(AUTHORIZATION_HEADER);
//    }
//    public String getHeaderTokenRefresh(HttpServletRequest request) {
//        String bearerToken = request.getHeader(REFRESH_TOKEN); {
//            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX))
//                return bearerToken.substring(7);
//        }
//        return request.getHeader(REFRESH_TOKEN);
//    }

    // 토큰 생성
    public TokenDto createAllToken(String email) {
        return new TokenDto(createToken(email, "Access"), createToken(email, "Refresh"));
    }
    public String createToken(String email, String type) {
        //토큰의 payload에 들어가는 유저정보가 많아서 유저의 정보를 간편하게 가져오기 위해 사용.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Date date = new Date();
        long time = type.equals("Access") ? getAccessTime() : getRefreshTime();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("userId", user.getId())
                        .claim("role", user.getRole())
                        .claim("username", user.getUsername())
                        .claim("companyName", user.getCompanies().getCompanyName())
                        .setExpiration(new Date(date.getTime() + time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }
    // 토큰 검증
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

    //리프레시 토큰 검증
    public Boolean refreshTokenValidation(String token) {
        //1차 토큰 검증
        if (!validateToken(token)) return false;

        //DB에 저장한 토큰 비교
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(getUserInfoFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());

    }

    //토큰의 정보를 추출(.getSubject()의 사용으로 반환타입을 Claims가 아닌 String으로 사용)
    @Transactional
    public String getUserInfoFromToken(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    //스프링 시큐리티 인증객체 생성
    @Transactional
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //  토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(AUTHORIZATION_HEADER, accessToken);  //리프레시 토큰의 헤더와 일관성을 위해 사용
    }

    //토큰 만료시간 static변수 -> 메서드
    public long getAccessTime() {
        return 2 * 60 * 60 * 1000L; //2시간
    }

    public long getRefreshTime() {
        return 60 * 60 * 24 * 7 * 1000L;    //7일
    }
}