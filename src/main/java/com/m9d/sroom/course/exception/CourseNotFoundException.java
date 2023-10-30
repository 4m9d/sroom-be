package com.m9d.sroom.course.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class CourseNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당하는 course 정보가 없습니다.";

    public CourseNotFoundException() {
        super(MESSAGE);
    }
}
