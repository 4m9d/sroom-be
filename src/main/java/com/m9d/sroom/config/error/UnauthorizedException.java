package com.m9d.sroom.config.error;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public abstract class UnauthorizedException extends RuntimeException {

    private final int statusCode = HttpStatus.UNAUTHORIZED.value();
    private String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }
}
