package com.m9d.sroom.member.exception;

import com.m9d.sroom.config.error.UnauthorizedException;

public class NoAuthorizationTokenException extends UnauthorizedException {

    private static final String MESSAGE = "ACCESS TOKEN이 입력되지 않았습니다.";

    public NoAuthorizationTokenException() {
        super(MESSAGE);
    }
}
