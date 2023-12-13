package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "COURSE_DAILY_LOG")
public class CourseDailyLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseDailyLogId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @Temporal(value = TemporalType.DATE)
    @CreationTimestamp
    private Date dailyLogDate;

    private Integer learningTime;

    private Integer quizCount;

    private Integer lectureCount;
}
