package com.m9d.sroom.lecture.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class PlaylistItemNotFoundException extends NotFoundException {

    private static final String MESSAGE = "입력한 lectureId에 해당하는 재생목록이 없습니다.";

    public PlaylistItemNotFoundException() {
        super(MESSAGE);
    }
}
