package com.m9d.sroom.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class InvalidParameterException extends RuntimeException {
    private final int statusCode = HttpStatus.NOT_FOUND.value();
    private String message;

    public InvalidParameterException(String message) {
        this.message = message;
    }
}
