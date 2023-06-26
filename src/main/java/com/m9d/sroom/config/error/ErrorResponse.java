package com.m9d.sroom.config.error;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private String statusCode;
    private String message;

    @Builder
    public ErrorResponse(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
