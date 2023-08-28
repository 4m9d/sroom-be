package com.m9d.sroom.config;

import com.m9d.sroom.member.exception.TokenExpiredException;
import com.m9d.sroom.member.exception.InvalidAccessTokenException;
import com.m9d.sroom.member.exception.NoAuthorizationTokenException;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Request URI = {}, HTTP Method = {}", request.getRequestURI(), request.getMethod());

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }

        if (StringUtils.equals(request.getMethod(), "OPTIONS")) {
            log.debug("if request options method is options, return true");

            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null) {
            log.debug("No authorization token found");
            throw new NoAuthorizationTokenException();
        }

        try {
            Map<String, Object> jwtDetail = jwtUtil.getDetailFromToken(token);

            if ((Long) jwtDetail.get("expirationTime") <= System.currentTimeMillis() / 1000) {
                log.debug("Token has expired");
                throw new TokenExpiredException();
            }

            request.setAttribute("memberId", jwtDetail.get("memberId"));

        } catch (Exception e) {
            log.error("Error validating token", e);
            throw new InvalidAccessTokenException();
        }

        return true;
    }
}