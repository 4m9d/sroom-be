package com.m9d.sroom.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DuplicationException extends RuntimeException {

    private final int statusCode = HttpStatus.NOT_FOUND.value();
    private String message;

    public DuplicationException(String message) {
        this.message = message;
    }
}
