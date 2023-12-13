package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "REVIEW")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private LectureEntity lecture;

    private String sourceCode;

    private Integer submittedRating;

    private String content;

    @CreationTimestamp
    private Timestamp submittedDate;
}
