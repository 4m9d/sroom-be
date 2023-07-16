package com.m9d.sroom.config.error;

import lombok.Getter;

import java.net.HttpURLConnection;

@Getter
public abstract class NotFoundException extends RuntimeException {

    private final int statusCode = HttpURLConnection.HTTP_NOT_FOUND;
    private String message;

    public NotFoundException(String message) {
        this.message = message;
    }
}
