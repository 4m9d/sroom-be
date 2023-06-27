package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.config.error.MissingParamException;

public class SearchKeywordMissingParamException extends MissingParamException {

    private static final String MESSAGE = "요청이 유효하지 않습니다 - 필수 파라미터 { keyword } 가 누락되었습니다.";

    public SearchKeywordMissingParamException(String message) {
        super(message);
    }
}
