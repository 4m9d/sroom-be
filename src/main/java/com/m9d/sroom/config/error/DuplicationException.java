package com.m9d.sroom.config.error;

import lombok.Getter;

@Getter
public class DuplicationException extends RuntimeException {

    private final String statusCode = "400";
    private String message;

    public DuplicationException(String message) {
        this.message = message;
    }
}
