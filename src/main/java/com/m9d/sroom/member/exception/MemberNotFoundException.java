package com.m9d.sroom.member.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 member를 찾을 수 없습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }
}
