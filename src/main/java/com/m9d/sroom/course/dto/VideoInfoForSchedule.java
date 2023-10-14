package com.m9d.sroom.course.dto;

import com.m9d.sroom.common.dto.CourseVideoDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoInfoForSchedule {

    private CourseVideoDto courseVideoDto;

    private Integer duration;
}
