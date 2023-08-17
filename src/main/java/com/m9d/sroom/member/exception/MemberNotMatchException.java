package com.m9d.sroom.member.exception;

import com.m9d.sroom.global.error.NotMatchException;

public class MemberNotMatchException extends NotMatchException {

    private static final String MESSAGE = "access token과 refresh token의 멤버 정보가 다릅니다.";
    public MemberNotMatchException() {
        super(MESSAGE);
    }
}
