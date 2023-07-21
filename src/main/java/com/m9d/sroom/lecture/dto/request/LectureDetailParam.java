package com.m9d.sroom.lecture.dto.request;

import lombok.Getter;

@Getter
public class LectureDetailParam {
    private boolean indexOnly = false;
    private int indexLimit = 50;
    private String indexNextToken;
    private boolean reviewOnly = false;
    private int reviewOffset = 0;
    private int reviewLimit = 10;
}
