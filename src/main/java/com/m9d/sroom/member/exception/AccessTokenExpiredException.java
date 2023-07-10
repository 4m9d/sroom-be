package com.m9d.sroom.member.exception;

import com.m9d.sroom.config.error.UnauthorizedException;

public class AccessTokenExpiredException extends UnauthorizedException {

    private static final String MESSAGE = "ACCESS TOKEN이 만료되었습니다.";

    public AccessTokenExpiredException() {
        super(MESSAGE);
    }
}
