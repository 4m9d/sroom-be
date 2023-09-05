package com.m9d.sroom.material.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseQuizInfo {

    private Long courseId;

    private Long quizId;

    private Long videoId;

    private Long courseVideoId;
}
