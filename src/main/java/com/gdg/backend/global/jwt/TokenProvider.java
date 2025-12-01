package com.gdg.backend.global.jwt;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.auth.dto.kakao.KakaoTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private final Key key;
    private final Long accessTokenValiditySeconds;
    private final Long refreshTokenValiditySeconds;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValiditySeconds,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValiditySeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public KakaoTokenDto createToken(User user, String getKakaoToken) {
        Long nowTime = new Date().getTime();

        Date expiryDate = new Date(nowTime + accessTokenValiditySeconds);
        Date refreshDate = new Date(nowTime + refreshTokenValiditySeconds);

        String accessToken = Jwts.builder()
                .setSubject(user.getId().toString()) // user email
                .claim("role", user.getRole().name()) //role : role에 따라 변경
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(user.getId().toString())
                .setExpiration(refreshDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return KakaoTokenDto.builder()
                .tokenType("bearer ") // 토큰 타입은 bearer
                .accessToken(accessToken) //accessToken저장
                .idToken(getKakaoToken) //idToken을 카카오 서버에서 생성한 토큰
                .expiresIn(accessTokenValiditySeconds / 1000) // accessToken 만료시간 / 초단위
                .refreshToken(refreshToken) //refreshToken 저장
                .refreshTokenExpiresIn(refreshTokenValiditySeconds / 1000) // refreshToken 만료시간 / 초단위
                .scope(user.getRole().name()) // 역할
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }

    public String revokeToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | SignatureException | MalformedJwtException e) {
            return false;
        } catch (ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
