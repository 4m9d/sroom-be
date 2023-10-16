package com.m9d.sroom.course.dto;

import com.m9d.sroom.common.dto.CourseVideo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoInfoForSchedule {

    private CourseVideo courseVideo;

    private Integer duration;
}
