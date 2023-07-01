package com.m9d.sroom.util;

import com.m9d.sroom.member.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JwtUtil {

    @Value("${secrets.jwt-secret}")
    private String jwtSecret;

    public String generateAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getMemberCode())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 유효 토큰
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getMemberCode())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7일 유효 토큰
                .signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
    }
}
