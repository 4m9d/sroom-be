package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Grading;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "COURSEQUIZ")
public class CourseQuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseQuizId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;

    @ManyToOne
    @JoinColumn(name = "course_video_id")
    private CourseVideoEntity courseVideo;


    private Boolean isScrapped;
    @Embedded
    private Grading grading;

    @PrePersist
    protected void onCreate() {
        if (grading != null) {
            grading.setSubmittedTime(new Timestamp(System.currentTimeMillis()));
        }
    }
}
