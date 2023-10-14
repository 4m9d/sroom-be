package com.m9d.sroom.material.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class SummaryNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 강의 노트를 찾을 수 없습니다.";

    public SummaryNotFoundException() {
        super(MESSAGE);
    }
}
