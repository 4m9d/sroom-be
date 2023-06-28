package com.m9d.sroom.lecture.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class Index {
    private int index;
    private String lectureId;
    private String lectureTitle;
    private int lectureDuration;

    @Builder
    public Index(int index, String lectureId, String lectureTitle, int lectureDuration) {
        this.index = index;
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
        this.lectureDuration = lectureDuration;
    }
}
