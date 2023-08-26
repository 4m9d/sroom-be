package com.m9d.sroom.lecture.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LectureDetailParam {

    private boolean index_only = false;

    private int index_limit = 50;

    private boolean review_only = false;

    private int review_offset = 0;

    private int review_limit = 10;
}
