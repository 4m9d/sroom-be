package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Lecture {

    private Long id;

    private Long memberId;

    private Long courseId;

    private Long sourceId;

    private Boolean playlist;

    private Integer lectureIndex;

    private Boolean reviewed;

    private String channel;
}
