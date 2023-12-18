package com.m9d.sroom.common.entity.jpa;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "REVIEW")
@Getter
@NoArgsConstructor
@DynamicInsert
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
