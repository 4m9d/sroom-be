package com.m9d.sroom.config.error;

import lombok.Getter;

@Getter
public abstract class InvalidParameterException extends RuntimeException {
    private final String statusCode = "400";
    private String message;

    public InvalidParameterException(String message) {
        this.message = message;
    }
}
