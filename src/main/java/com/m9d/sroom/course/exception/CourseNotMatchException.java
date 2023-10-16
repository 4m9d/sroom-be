package com.m9d.sroom.course.exception;

import com.m9d.sroom.common.error.NotMatchException;

public class CourseNotMatchException extends NotMatchException {

    private static final String MESSAGE = "멤버에게서 해당 course 정보를 찾을 수 없습니다.";

    public CourseNotMatchException() {
        super(MESSAGE);
    }
}
