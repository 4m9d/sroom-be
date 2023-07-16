package com.m9d.sroom.config.error;

import lombok.Getter;

import java.net.HttpURLConnection;

@Getter
public abstract class NotMatchException extends RuntimeException {

    private final int statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
    private String message;

    public NotMatchException(String message) {
        this.message = message;
    }
}
