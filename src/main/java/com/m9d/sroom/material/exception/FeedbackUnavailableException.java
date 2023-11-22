package com.m9d.sroom.material.exception;

import com.m9d.sroom.common.error.NotMatchException;

public class FeedbackUnavailableException extends NotMatchException {

    private static final String MESSAGE = "수정된 요약본은 사용자 피드백이 불가합니다.";

    public FeedbackUnavailableException() {
        super(MESSAGE);
    }
}
