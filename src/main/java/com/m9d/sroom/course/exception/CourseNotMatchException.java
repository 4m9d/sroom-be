package com.m9d.sroom.course.exception;

import com.m9d.sroom.config.error.NotMatchException;

public class CourseNotMatchException extends NotMatchException {

    private static final String MESSAGE = "해당 멤버에게서 입력된 courseId를 찾을 수 없습니다.";

    public CourseNotMatchException() {
        super(MESSAGE);
    }
}
