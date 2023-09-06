package com.m9d.sroom.material.exception;

import com.m9d.sroom.global.error.DuplicationException;

public class CourseQuizDuplicationException extends DuplicationException {

    private static final String MESSAGE = "이미 채점 결과가 저장된 퀴즈입니다.";

    public CourseQuizDuplicationException() {
        super(MESSAGE);
    }
}
