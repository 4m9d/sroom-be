package com.m9d.sroom.common.entity.jpa;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "COURSE_DAILY_LOG")
@Getter
@NoArgsConstructor(access = PROTECTED)
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

    private CourseDailyLogEntity(CourseEntity course, Integer learningTime, Integer quizCount, Integer lectureCount) {
        setMember(course.getMember());
        setCourse(course);
        this.dailyLogDate = new Date();
        this.learningTime = learningTime;
        this.quizCount = quizCount;
        this.lectureCount = lectureCount;
    }

    public static CourseDailyLogEntity create(CourseEntity course, Integer learningTime, Integer quizCount, Integer
            lectureCount) {
        return new CourseDailyLogEntity(course, learningTime, quizCount, lectureCount);
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            this.member.getDailyLogs().remove(this);
        }

        this.member = member;
        member.getDailyLogs().add(this);
    }

    private void setCourse(CourseEntity course) {
        if (this.course != null) {
            this.course.getDailyLogs().remove(this);
        }
        this.course = course;
        course.getDailyLogs().add(this);
    }
}
