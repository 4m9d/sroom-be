package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.config.error.InvalidParameterException;

public class VideoIndexParamException extends InvalidParameterException {

    private static final String MESSAGE = "동영상 강의의 경우 목차가 존재하지 않습니다.";
    public VideoIndexParamException() {
        super(MESSAGE);
    }
}
