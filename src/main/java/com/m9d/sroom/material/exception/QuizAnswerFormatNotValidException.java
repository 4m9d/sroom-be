package com.m9d.sroom.material.exception;

import com.m9d.sroom.global.error.InvalidParameterException;

public class QuizAnswerFormatNotValidException extends InvalidParameterException {

    private static final String MESSAGE = "입력받은 정답의 포멧이 적절하지 않습니다. 보기 번호 또는 true / false 를 반환해주세요";

    public QuizAnswerFormatNotValidException() {
        super(MESSAGE);
    }
}
