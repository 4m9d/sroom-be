package com.m9d.sroom.material.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseVideoKey {

    private Long courseId;

    private Long videoId;
}
