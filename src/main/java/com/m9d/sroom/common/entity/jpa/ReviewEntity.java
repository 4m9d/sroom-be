package com.m9d.sroom.common.entity.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "REVIEW")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @OneToOne
    @JoinColumn(name = "lecture_id")
    private LectureEntity lecture;

    private String sourceCode;

    private Integer submittedRating;

    private String content;

    @CreationTimestamp
    private Timestamp submittedDate;

    private ReviewEntity(LectureEntity lecture, String sourceCode, int submittedRating, String content) {
        setMember(lecture.getMember());
        this.lecture = lecture;
        this.sourceCode = sourceCode;
        this.submittedRating = submittedRating;
        this.content = content;
        this.submittedDate = new Timestamp(System.currentTimeMillis());
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            this.member.getReviews().remove(this);
        }

        member.getReviews().add(this);
        this.member = member;
    }

    public static ReviewEntity create(LectureEntity lecture, String sourceCode, int submittedRating, String content) {
        return new ReviewEntity(lecture, sourceCode, submittedRating, content);
    }

    public void removeLecture() {
        this.lecture = null;
    }
}
