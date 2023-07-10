package com.m9d.sroom.member.exception;

import com.m9d.sroom.config.error.UnauthorizedException;

public class InvalidAccessTokenException extends UnauthorizedException {

    private static final String MESSAGE = "부적절한 토큰입니다.";

    public InvalidAccessTokenException() {
        super(MESSAGE);
    }
}
