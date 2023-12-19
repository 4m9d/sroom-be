package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.MemberStats;
import com.m9d.sroom.course.CourseMapper;
import com.m9d.sroom.search.dto.response.CourseBrief;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String memberCode;

    private String memberName;

    private String refreshToken;


    @OneToMany(mappedBy = "member")
    private List<CourseEntity> courses = new ArrayList<CourseEntity>();

    @OneToMany(mappedBy = "member")
    private List<CourseDailyLogEntity> dailyLogs = new ArrayList<CourseDailyLogEntity>();

    @OneToMany(mappedBy = "member")
    private List<CourseQuizEntity> quizzes = new ArrayList<CourseQuizEntity>();

    @OneToMany(mappedBy = "member")
    private List<MaterialFeedbackEntity> feedbacks = new ArrayList<MaterialFeedbackEntity>();

    @Embedded
    private MemberStats stats;

    @CreationTimestamp
    private Timestamp signUpTime;

    private Boolean status;

    private String bio;

    @Builder
    private MemberEntity(String memberCode, String memberName) {
        this.memberCode = memberCode;
        this.memberName = memberName;
        this.refreshToken = "";
        this.stats = new MemberStats(0, 0, 0, 0);
        this.signUpTime = new Timestamp(System.currentTimeMillis());
        this.status = true;
        this.bio = "";
    }

    public void updateName(String newName) {
        this.memberName = newName;
    }

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    public List<CourseBrief> getCourseBriefList() {
        return courses.stream()
                .map(CourseMapper::getBriefByEntity)
                .collect(Collectors.toList());
    }

    public List<CourseEntity> getCoursesByLatestOrder() {
        return courses.stream()
                .sorted(Comparator.comparing(CourseEntity::isCompleted)
                        .thenComparing(CourseEntity::getLastViewTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<CourseDailyLogEntity> getLogList() {
        return courses.stream()
                .flatMap(course -> course.getDailyLogs().stream())
                .collect(Collectors.toList());
    }

    public Set<String> getVideoCodeSet() {
        return courses.stream()
                .flatMap(course -> course.getCourseVideos().stream())
                .map(courseVideo -> courseVideo.getVideo().getVideoCode())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
