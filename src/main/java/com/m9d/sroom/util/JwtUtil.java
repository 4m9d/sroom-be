package com.m9d.sroom.util;

import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {

    @Value("${jwt-secret}")
    private String jwtSecret;

    public static final long ACCESS_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 60 * 10; // 10시간 유효
    public static final long REFRESH_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 60 * 24 * 7; // 7일 유효
    public static final String EXPIRATION_TIME = "expirationTime";

    public String generateAccessToken(Member member) {
        return generateToken(member.getMemberId(), ACCESS_TOKEN_EXPIRATION_PERIOD);
    }

    public String generateRefreshToken(Member member) {
        return generateToken(member.getMemberId(), REFRESH_TOKEN_EXPIRATION_PERIOD);
    }

    private String generateToken(Object subject, long expirationPeriod) {
        return Jwts.builder()
                .setSubject(String.valueOf(subject))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationPeriod))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Map<String, Object> getDetailFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        }

        Map<String, Object> details = new HashMap<>();
        details.put("expirationTime", claims.getExpiration().getTime() / 1000);
        details.put("memberId", claims.getSubject());

        return details;
    }

    public Long getMemberIdFromRequest() {
        ServletRequestAttributes request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        return Long.valueOf((String) request.getRequest().getAttribute("memberId"));
    }
}
