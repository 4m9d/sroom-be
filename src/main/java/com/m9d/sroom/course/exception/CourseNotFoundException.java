package com.m9d.sroom.course.exception;

import com.m9d.sroom.global.error.NotFoundException;

public class CourseNotFoundException extends NotFoundException {

    private static final String MESSAGE = "입력받은 courseId가 존재하지 않습니다";

    public CourseNotFoundException() {
        super(MESSAGE);
    }
}
