package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class LectureNotFoundException extends NotFoundException {

    private static final String MESSAGE = "잘못된 입력입니다. 강의를 찾을 수 없습니다.";

    public LectureNotFoundException() {
        super(MESSAGE);
    }
}
