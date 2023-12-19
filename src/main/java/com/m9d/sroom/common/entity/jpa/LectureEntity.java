package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "LECTURE")
public class LectureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    private Long sourceId;

    private Boolean isPlaylist;

    private Integer lectureIndex;

    private Boolean isReviewed;

    private String channel;

    @OneToOne
    @JoinColumn(name = "review_id")
    private ReviewEntity review;
}
