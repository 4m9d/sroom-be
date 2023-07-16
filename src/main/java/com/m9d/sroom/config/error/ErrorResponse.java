package com.m9d.sroom.config.error;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class ErrorResponse {

    private int statusCode;
    private String message;

    @Builder
    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
