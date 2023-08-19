package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.global.error.NotFoundException;

public class PlaylistNotFoundException extends NotFoundException {

    private static final String MESSAGE = "입력한 lectureId에 해당하는 재생목록이 없습니다.";
    public PlaylistNotFoundException() {
        super(MESSAGE);
    }
}
