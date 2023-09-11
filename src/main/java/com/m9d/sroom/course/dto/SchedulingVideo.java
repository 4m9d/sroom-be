package com.m9d.sroom.course.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchedulingVideo {

    private Long videoId;

    private int index;

    private int duration;
}
