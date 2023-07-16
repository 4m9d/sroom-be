package com.m9d.sroom.config.error;

import lombok.Getter;

import java.net.HttpURLConnection;

@Getter
public abstract class UnauthorizedException extends RuntimeException {

    private final int statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
    private String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }
}
