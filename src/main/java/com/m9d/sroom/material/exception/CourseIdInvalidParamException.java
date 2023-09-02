package com.m9d.sroom.material.exception;

import com.m9d.sroom.global.error.InvalidParameterException;

public class CourseIdInvalidParamException extends InvalidParameterException {

    private static final String MESSAGE = "courseId 가 입력되지 않았습니다. body 에 담아주세요.";

    public CourseIdInvalidParamException() {
        super(MESSAGE);
    }
}
