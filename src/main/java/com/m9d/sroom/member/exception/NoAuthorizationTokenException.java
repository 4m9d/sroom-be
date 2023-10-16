package com.m9d.sroom.member.exception;

import com.m9d.sroom.common.error.UnauthorizedException;

public class NoAuthorizationTokenException extends UnauthorizedException {

    private static final String MESSAGE = "access token이 입력되지 않았습니다.";

    public NoAuthorizationTokenException() {
        super(MESSAGE);
    }
}
