package com.gdg.backend.global.jwt;

import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.user.domain.User;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TokenProvider {
    private final Key key;
    @Getter
    private final Integer accessTokenValiditySeconds;
    @Getter
    private final Integer refreshTokenValiditySeconds;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.access-token-validity-in-milliseconds}") Integer accessTokenValiditySeconds,
                         @Value("${jwt.refresh-token-validity-in-milliseconds}") Integer refreshTokenValiditySeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        log.info("JWT SECRET RAW = {}", secretKey);
        log.info("JWT SECRET DECODED LENGTH = {}", Decoders.BASE64.decode(secretKey).length);
    }

    public String createToken(User user, Integer seconds) {
        Long nowTime = new Date().getTime();

        Date expiryDate = new Date(nowTime + seconds);

        return Jwts.builder()
                .setSubject(user.getId().toString()) // user email
                .claim("role", user.getRole().name()) //role : role에 따라 변경
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String accessToken(User user){
        return createToken(user, accessTokenValiditySeconds);
    }

    public String refreshToken(User user){
        return createToken(user, refreshTokenValiditySeconds);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        String role = claims.get("role").toString();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserPrincipal principal = new UserPrincipal(
                Long.valueOf(claims.getSubject()),
                role
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );
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
