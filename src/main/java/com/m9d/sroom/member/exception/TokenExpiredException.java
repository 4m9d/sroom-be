package com.m9d.sroom.member.exception;

import com.m9d.sroom.global.error.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {

    private static final String MESSAGE = "token이 만료되었습니다.";

    public TokenExpiredException() {
        super(MESSAGE);
    }
}
