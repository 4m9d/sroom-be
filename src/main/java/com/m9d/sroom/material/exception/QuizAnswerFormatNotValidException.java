package com.m9d.sroom.material.exception;

import com.m9d.sroom.common.error.InvalidParameterException;

public class QuizAnswerFormatNotValidException extends InvalidParameterException {

    private static final String MESSAGE = "입력받은 정답의 포멧이 적절하지 않습니다. 보기 번호(1~5) 또는 true / false, 또는 주관식 답을 적절히 반환해 주세요. is_correct도 null이 허용되지 않습니다.";

    public QuizAnswerFormatNotValidException() {
        super(MESSAGE);
    }
}
