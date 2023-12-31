package com.m9d.sroom.util;

import com.m9d.sroom.member.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public static final long ACCESS_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 30; // 30분 유효
    public static final long REFRESH_TOKEN_EXPIRATION_PERIOD = 1000L * 60 * 60 * 24 * 3; // 3일 유효
    public static final String EXPIRATION_TIME = "expirationTime";

    public String generateAccessToken(Long memberId, String pictureUrl) {
        return generateToken(memberId, pictureUrl, ACCESS_TOKEN_EXPIRATION_PERIOD);
    }

    public String generateRefreshToken(Long memberId, String pictureUrl) {
        return generateToken(memberId, pictureUrl, REFRESH_TOKEN_EXPIRATION_PERIOD);
    }

    private String generateToken(Long memberId, String pictureUrl, long expirationPeriod) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationPeriod))
                .claim("profile", pictureUrl)
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
        details.put("profile", claims.get("profile"));

        return details;
    }

    public Long getMemberIdFromRequest() {
        ServletRequestAttributes request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        return Long.valueOf((String) request.getRequest().getAttribute("memberId"));
    }
}
