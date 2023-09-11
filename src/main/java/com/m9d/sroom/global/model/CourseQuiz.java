package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseQuiz {

    private Long id;

    private Long quizId;

    private Long courseId;

    private Long videoId;

    private Long courseVideoId;

    private int submittedAnswer;

    private Boolean correct;

    private Boolean scrapped;
}
