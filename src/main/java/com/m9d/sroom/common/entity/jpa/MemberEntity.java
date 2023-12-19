package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.MemberStats;
import com.m9d.sroom.course.CourseMapper;
import com.m9d.sroom.search.dto.response.CourseBrief;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.Member;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
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

    @Embedded
    private MemberStats stats;

    @CreationTimestamp
    private Timestamp signUpTime;

    private Boolean status;

    private String bio;

    @OneToMany(mappedBy = "member")
    private List<CourseEntity> courses = new ArrayList<CourseEntity>();

    @OneToMany(mappedBy = "member")
    private List<CourseVideoEntity> courseVideos = new ArrayList<CourseVideoEntity>();

    @OneToMany(mappedBy = "member")
    private List<CourseDailyLogEntity> dailyLogs = new ArrayList<CourseDailyLogEntity>();

    @OneToMany(mappedBy = "member")
    private List<MaterialFeedbackEntity> feedbacks = new ArrayList<MaterialFeedbackEntity>();

    @OneToMany(mappedBy = "member")
    private List<LectureEntity> lectures = new ArrayList<LectureEntity>();

    @OneToMany(mappedBy = "member")
    private List<CourseQuizEntity> courseQuizzes = new ArrayList<CourseQuizEntity>();

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
                        .thenComparing(CourseEntity::getLastViewTime, Comparator.reverseOrder())
                        .thenComparing(CourseEntity::getCourseId, Comparator.reverseOrder()))
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

    public Optional<MaterialFeedbackEntity> findFeedbackByMaterialIdAndType(int materialType, Long materialId) {
        return feedbacks.stream()
                .filter(f -> f.getContentType().equals(materialType))
                .filter(f -> f.getContentId().equals(materialId))
                .findFirst();
    }

    public List<String> getChannelListOrderByCount() {
        Map<String, Integer> channelCounts = new HashMap<>();
        for (LectureEntity lecture : lectures) {
            channelCounts.put(
                    lecture.getChannel(), channelCounts.getOrDefault(lecture.getChannel(), 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedEntryList = channelCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        return sortedEntryList.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<CourseQuizEntity> getWrongQuizList(int limit) {
        return courseQuizzes.stream()
                .filter(q -> q.getGrading().getIsCorrect().equals(false))
                .sorted(Comparator.comparing((CourseQuizEntity q) -> q.getGrading().getSubmittedTime()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
