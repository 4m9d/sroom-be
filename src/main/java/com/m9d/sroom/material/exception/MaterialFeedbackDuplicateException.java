package com.m9d.sroom.material.exception;

import com.m9d.sroom.common.error.DuplicationException;

public class MaterialFeedbackDuplicateException extends DuplicationException {

    private static final String MESSAGE = "이미 피드백된 강의자료입니다.";

    public MaterialFeedbackDuplicateException() {
        super(MESSAGE);
    }
}
