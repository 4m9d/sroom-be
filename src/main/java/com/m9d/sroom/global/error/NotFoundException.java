package com.m9d.sroom.global.error;

import lombok.Getter;

import org.springframework.http.HttpStatus;
@Getter
public abstract class NotFoundException extends RuntimeException {

    private final int statusCode = HttpStatus.NOT_FOUND.value();
    private String message;

    public NotFoundException(String message) {
        this.message = message;
    }
}
