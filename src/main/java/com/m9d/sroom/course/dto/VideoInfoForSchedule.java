package com.m9d.sroom.course.dto;

import com.m9d.sroom.common.entity.jdbctemplate.CourseVideoEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoInfoForSchedule {

    private CourseVideoEntity courseVideo;

    private Integer duration;
}
