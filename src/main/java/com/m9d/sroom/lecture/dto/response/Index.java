package com.m9d.sroom.lecture.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class Index {
    private int index;
    private String lectureId;
    private String thumbnail;
    private String lectureTitle;

    @Builder
    public Index(int index, String lectureId, String thumbnail, String lectureTitle) {
        this.index = index;
        this.lectureId = lectureId;
        this.thumbnail = thumbnail;
        this.lectureTitle = lectureTitle;
    }
}
