package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.LearningStatus;
import com.m9d.sroom.common.entity.jpa.embedded.Sequence;
import com.m9d.sroom.search.dto.response.VideoWatchInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "COURSEVIDEO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
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

    @OneToMany(mappedBy = "courseVideo")
    private List<CourseQuizEntity> courseQuizzes = new ArrayList<CourseQuizEntity>();

    @PreUpdate
    protected void onUpdate() {
        if (status != null) {
            status.setLastViewTime(new Timestamp(System.currentTimeMillis()));
        }
    }

    private CourseVideoEntity(CourseEntity course, VideoEntity video, LectureEntity lecture, SummaryEntity summary,
                              int section, int videoIndex) {
        this.video = video;
        if (summary != null) {
            this.summary = summary;  // @DynamicInsert를 통해 summary == null인 경우에는 입력되지 않습니다.
        }
        this.sequence = new Sequence(section, videoIndex, lecture.getLectureIndex());
        this.status = new LearningStatus(0, false, null, 0);
        setMember(course.getMember());
        setCourse(course);
        setLecture(lecture);
    }


    private void setCourse(CourseEntity course) {
        if (this.course != null) {
            this.course.getCourseVideos().remove(this);
        }

        this.course = course;
        course.getCourseVideos().add(this);
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            this.member.getCourseVideos().remove(this);
        }

        this.member = member;
        member.getCourseVideos().add(this);
    }

    private void setLecture(LectureEntity lecture) {
        if (this.lecture != null) {
            this.lecture.getCourseVideos().remove(this);
        }

        this.lecture = lecture;
        lecture.getCourseVideos().add(this);
    }

    public static CourseVideoEntity create(CourseEntity course, VideoEntity video,
                                           LectureEntity lecture, SummaryEntity summary, int section, int videoIndex) {
        return new CourseVideoEntity(course, video, lecture, summary, section, videoIndex);
    }

    public Optional<CourseQuizEntity> findCourseQuizByQuizId(Long quizId) {
        return courseQuizzes.stream()
                .filter(courseQuiz -> courseQuiz.getQuiz().getQuizId().equals(quizId))
                .findFirst();
    }

    public void updateSection(int section) {
        this.sequence.setSection(section);
    }

    public void updateStatus(LearningStatus learningStatus) {
        this.status = learningStatus;
    }
}
