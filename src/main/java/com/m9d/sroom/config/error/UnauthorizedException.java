package com.m9d.sroom.config.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public abstract class UnauthorizedException extends RuntimeException {

    private final String statusCode = "401";
    private String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }
}
