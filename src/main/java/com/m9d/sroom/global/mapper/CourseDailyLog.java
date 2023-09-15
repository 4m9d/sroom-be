package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class CourseDailyLog {

    private Long courseDailyLogId;

    private Long memberId;

    private Long courseId;

    private Date dailyLogDate;

    private int learningTime;

    private int quizCount;

    private int lectureCount;
}
