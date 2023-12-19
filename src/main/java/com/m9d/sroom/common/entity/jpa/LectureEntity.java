package com.m9d.sroom.common.entity.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LECTURE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "lecture")
    private List<CourseVideoEntity> courseVideos = new ArrayList<CourseVideoEntity>();

    private LectureEntity(MemberEntity member, CourseEntity course, Long sourceId, Boolean isPlaylist,
                          Integer lectureIndex, String channel) {
        setMember(member);
        setCourse(course);
        this.sourceId = sourceId;
        this.isPlaylist = isPlaylist;
        this.lectureIndex = lectureIndex;
        this.channel = channel;
        this.isReviewed = false;
    }

    private void setCourse(CourseEntity course) {
        if (this.course != null) {
            this.course.getLectures().remove(this);
        }

        course.getLectures().add(this);
        this.course = course;
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            this.member.getLectures().remove(this);
        }

        member.getLectures().add(this);
        this.member = member;
    }

    public static LectureEntity create(MemberEntity member, CourseEntity course, Long sourceId, Boolean isPlaylist,
                                       Integer lectureIndex, String channel) {
        return new LectureEntity(member, course, sourceId, isPlaylist, lectureIndex, channel);
    }
}
