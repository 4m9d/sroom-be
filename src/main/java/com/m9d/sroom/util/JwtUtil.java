package com.m9d.sroom.util;

import com.m9d.sroom.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JwtUtil {

    @Value("${jwt-secret}")
    private String jwtSecret;

    private static final long ACCESS_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 60 * 10; // 10시간
    private static final long REFRESH_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 60 * 24 * 7; // 7일

    public String generateAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getMemberCode())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getMemberCode())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_PERIOD)) // 7일 유효 토큰
                .signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
    }

    public Long getExpirationTimeFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().getTime() / 1000; // convert to Unix time
    }
}
