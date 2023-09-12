package com.m9d.sroom.course.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoInfoForSchedule {

    private Long courseVideoId;

    private Long videoId;

    private Integer videoIndex;

    private Integer duration;
}
