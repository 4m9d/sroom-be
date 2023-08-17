package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.global.error.InvalidParameterException;

public class TwoOnlyParamTrueException extends InvalidParameterException {

    private static final String MESSAGE = "reviewOnly와 indexOnly를 동시에 true로 설정할 수 없습니다.";

    public TwoOnlyParamTrueException() {
        super(MESSAGE);
    }
}
