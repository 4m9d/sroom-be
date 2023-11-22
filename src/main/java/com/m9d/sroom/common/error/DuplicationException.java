package com.m9d.sroom.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DuplicationException extends RuntimeException {

    private final int statusCode = HttpStatus.BAD_REQUEST.value();
    private String message;

    public DuplicationException(String message) {
        this.message = message;
    }
}
