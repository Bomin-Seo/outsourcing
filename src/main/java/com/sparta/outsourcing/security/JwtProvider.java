package com.sparta.outsourcing.security;

import com.sparta.outsourcing.dto.TokenDto;
import com.sparta.outsourcing.enums.AuthEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final long TOKEN_TIME = 30 * 60 * 100000000000L; // 30분
    private final long REFRESH_TOKEN_TIME = 14 * 24* 60 * 60 * 1000L; // 2주

    private final UserDetailsServiceImpl userDetailsService;
    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenDto createToken(Authentication authentication) {

        long now = (new Date()).getTime();

        String accessToken = AuthEnum.GRANT_TYPE.getValue() +
                Jwts.builder()
                        .setSubject("엑세스") // subject 설정
                        .claim(authentication.getName(),
                                authentication.getAuthorities()) // 사용자 식별자값(ID)
                        .setExpiration(new Date(now + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(new Date(now)) // 발급일
                        .signWith(key, signatureAlgorithm)// 암호화 알고리즘
                        .setIssuer(authentication.getName())
                        .compact();

        String refreshToken = AuthEnum.GRANT_TYPE.getValue() +
                Jwts.builder()
                        .setSubject("리프레시")
                        .claim(authentication.getName(), authentication.getAuthorities())
                        .setExpiration(new Date(now + REFRESH_TOKEN_TIME))
                        .setIssuedAt(new Date(now))
                        .signWith(key, signatureAlgorithm)
                        .setIssuer(authentication.getName())
                        .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
      }



    /**
     * 토큰에서 유저 정보 추출
     */
    public Authentication getAuthentication(String token) {
    UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(getUsername(token));
    if (!userDetails.isAccountNonExpired()) {
        throw new AccountExpiredException("만료된 토큰입니다.");
    }
    return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
            userDetails.getAuthorities());
}


/**
 * 토큰 정보 검증
 */
public boolean validateToken(String token) {
    log.info("validateToken start");
    log.info("token: {}", token);
    try {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
        log.info("Invalid JWT Token", e);
    } catch (ExpiredJwtException e) {
        // refresh token 활용해서 재발급
        log.info("Expired JWT Token", e);
        throw e;
    } catch (UnsupportedJwtException e) {
        log.info("Unsupported JWT Token", e);
    } catch (IllegalArgumentException e) {
        log.info("JWT claims string is empty.", e);
    }
    return false;
}

private Claims parseClaims(String token) {
    try {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    } catch (ExpiredJwtException e) {
        return e.getClaims();
    }
}
    public String getUsername(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return claims.getIssuer();
    }


}
