package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.global.error.NotFoundException;

public class VideoNotFoundException extends NotFoundException {

    private static final String MESSAGE = "입력한 정보에 해당하는 영상이 없습니다.";

    public VideoNotFoundException() {
        super(MESSAGE);
    }
}
