package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Lecture {

    private Long id;

    private Long memberId;

    private Long courseId;

    private Long sourceId;

    private boolean playlist;

    private int index;

    private Boolean reviewed;

    private String channel;
}
