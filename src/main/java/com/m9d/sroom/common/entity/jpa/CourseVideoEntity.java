package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.LearningStatus;
import com.m9d.sroom.common.entity.jpa.embedded.Sequence;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "COURSEVIDEO")
public class CourseVideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseVideoId;

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
    @JoinColumn(name = "lecture_id")
    private LectureEntity lecture;

    @ManyToOne
    @JoinColumn(name = "summary_id")
    private SummaryEntity summary;

    @Embedded
    private Sequence sequence;

    @Embedded
    private LearningStatus status;

    @PreUpdate
    protected void onUpdate() {
        if (status != null) {
            status.setLastViewTime(new Timestamp(System.currentTimeMillis()));
        }
    }
}
