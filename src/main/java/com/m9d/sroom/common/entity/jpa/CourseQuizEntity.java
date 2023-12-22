package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Grading;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "COURSEQUIZ")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private CourseQuizEntity(CourseVideoEntity courseVideo, QuizEntity quiz, String submittedAnswer,
                             boolean isCorrect) {
        this.video = quiz.getVideo();
        this.quiz = quiz;
        this.isScrapped = false;
        this.grading = new Grading(submittedAnswer, isCorrect, new Timestamp(System.currentTimeMillis()));
        setMember(courseVideo.getMember());
        setCourse(courseVideo.getCourse());
        setCourseVideo(courseVideo);
    }

    private void setCourseVideo(CourseVideoEntity courseVideo) {
        if (this.courseVideo != null) {
            this.courseVideo.getCourseQuizzes().remove(this);
        }

        courseVideo.getCourseQuizzes().add(this);
        this.courseVideo = courseVideo;
    }

    private void setCourse(CourseEntity course) {
        if (this.course != null) {
            this.course.getCourseQuizzes().remove(this);
        }

        course.getCourseQuizzes().add(this);
        this.course = course;
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            this.member.getCourseQuizzes().remove(this);
        }

        member.getCourseQuizzes().add(this);
        this.member = member;
    }

    public static CourseQuizEntity create(CourseVideoEntity courseVideo, QuizEntity quiz, String submittedAnswer,
                                          boolean isCorrect) {
        return new CourseQuizEntity(courseVideo, quiz, submittedAnswer, isCorrect);
    }
}
