package com.m9d.sroom.member.exception;

import com.m9d.sroom.global.error.UnauthorizedException;

public class RefreshRenewedException extends UnauthorizedException {

    private static final String MESSAGE = "refresh token이 갱신되었습니다. 다시 로그인해주세요";
    public RefreshRenewedException() {
        super(MESSAGE);
    }
}
