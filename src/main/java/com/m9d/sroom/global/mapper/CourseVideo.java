package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class CourseVideo {

    private Long courseVideoId;

    private Long courseId;

    private Long videoId;

    private int section;

    private int videoIndex;

    private int startTime;

    private boolean complete;

    private Long summaryId;

    private int lectureIndex;

    private Long memberId;

    private Timestamp lastViewTime;

    private int maxDuration;
}
