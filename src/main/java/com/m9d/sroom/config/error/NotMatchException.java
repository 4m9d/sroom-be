package com.m9d.sroom.config.error;

import lombok.Getter;

@Getter
public abstract class NotMatchException extends RuntimeException {

    private final String statusCode = "400";
    private String message;

    public NotMatchException(String message) {
        this.message = message;
    }
}
