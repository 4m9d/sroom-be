package com.m9d.sroom.course.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class CourseVideoNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 코스에서 해당 강의를 찾을 수 없습니다.";

    public CourseVideoNotFoundException() {
        super(MESSAGE);
    }
}
