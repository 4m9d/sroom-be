package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter @Setter
@Builder
public class Course {

    private Long courseId;

    private Long memberId;

    private int videoCount;

    private String title;

    private String lectureCount;

    private String thumbnail;

    private int duration;

    private Timestamp expectedEndTime;

    private int dailyTargetTime;

    private int weeks;

    private boolean scheduled;

    private Date startDate;
}
