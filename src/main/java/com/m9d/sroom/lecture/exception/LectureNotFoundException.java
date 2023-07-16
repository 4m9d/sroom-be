package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.config.error.NotFoundException;

public class LectureNotFoundException extends NotFoundException {

    private static final String MESSAGE = "입력한 lectureId에 해당하는 강의가 없습니다.";

    public LectureNotFoundException() {
        super(MESSAGE);
    }
}
