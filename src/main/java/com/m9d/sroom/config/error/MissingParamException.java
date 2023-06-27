package com.m9d.sroom.config.error;

import lombok.Getter;

@Getter
public class MissingParamException {

    private final String statusCode = "400";
    private String message;

    public MissingParamException(String message) {
        this.message = message;
    }
}

