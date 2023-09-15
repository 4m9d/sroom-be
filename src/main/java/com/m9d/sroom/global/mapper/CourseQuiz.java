package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class CourseQuiz {

    private Long id;

    private Long courseId;

    private Long courseVideoId;

    private Long quizId;

    private Long videoId;

    private String submittedAnswer;

    private Boolean correct;

    private Boolean scrapped;

    private Timestamp submittedTime;
}
