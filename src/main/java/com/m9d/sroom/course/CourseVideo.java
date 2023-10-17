package com.m9d.sroom.course;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CourseVideo {

    private final Long videoId;

    private final Long summaryId;

    @Setter
    private int section;

    private final int videoIndex;

    private final int lectureIndex;

    public CourseVideo(Long videoId, Long summaryId, int section, int videoIndex, int lectureIndex) {
        this.videoId = videoId;
        this.section = section;
        this.summaryId = summaryId;
        this.videoIndex = videoIndex;
        this.lectureIndex = lectureIndex;
    }
}
