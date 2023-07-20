package com.m9d.sroom.config.error;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class ErrorResponse {

    private int statusCode;
    private String message;
}
