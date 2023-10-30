package com.m9d.sroom.member.exception;

import com.m9d.sroom.common.error.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {

    private static final String MESSAGE = "token이 만료되었습니다.";

    public TokenExpiredException() {
        super(MESSAGE);
    }
}
