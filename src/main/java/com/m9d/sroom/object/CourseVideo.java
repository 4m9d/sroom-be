package com.m9d.sroom.object;

import lombok.Getter;

@Getter
public class CourseVideo extends VideoSaved {

    private final Integer section;

    private final Integer videoIndex;

    private final Integer lectureIndex;

    public CourseVideo(VideoSaved videoSaved, Integer section, Integer videoIndex, Integer lectureIndex) {
        super(videoSaved.getVideoDto());
        this.section = section;
        this.videoIndex = videoIndex;
        this.lectureIndex = lectureIndex;
    }
}
