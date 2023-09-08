package com.m9d.sroom.material.exception;

import com.m9d.sroom.global.error.NotFoundException;

public class CourseQuizNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 courseQuiz를 찾을 수 없습니다.";


    public CourseQuizNotFoundException() {
        super(MESSAGE);
    }
}
