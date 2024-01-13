package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.MemberStats;
import com.m9d.sroom.material.model.MaterialType;
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

    @OneToMany(mappedBy = "member")
    private List<ReviewEntity> reviews = new ArrayList<ReviewEntity>();

    private MemberEntity(String memberCode, String memberName) {
        this.memberCode = memberCode;
        this.memberName = memberName;
        this.refreshToken = "";
        this.stats = new MemberStats(0, 0, 0, 0);
        this.signUpTime = new Timestamp(System.currentTimeMillis());
        this.status = true;
        this.bio = "";
    }

    public static MemberEntity create(String memberCode, String memberName) {
        return new MemberEntity(memberCode, memberName);
    }

    public void updateName(String newName) {
        this.memberName = newName;
    }

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    public void addLearningTime(int newLearningTime) {
        this.stats.setTotalLearningTime(this.stats.getTotalLearningTime() + newLearningTime);
    }

    public void updateCompletionRate() {
        double completedCourseCount = (double) courses.stream()
                .filter(courseEntity -> courseEntity.getProgress() == 100)
                .count();

        this.stats.setCompletionRate((int) (completedCourseCount / courses.size()));
    }


    public List<CourseEntity> getCoursesByLatestOrder() {
        return courses.stream()
                .sorted(Comparator.comparing(CourseEntity::isCompleted)
                        .thenComparing(CourseEntity::getLastViewTime, Comparator.reverseOrder())
                        .thenComparing(CourseEntity::getCourseId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public Set<String> getVideoCodeSet() {
        return courses.stream()
                .flatMap(course -> course.getCourseVideos().stream())
                .map(courseVideo -> courseVideo.getVideo().getVideoCode())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Optional<MaterialFeedbackEntity> findFeedbackByMaterialIdAndType(MaterialType materialType, Long materialId) {
        return feedbacks.stream()
                .filter(f -> f.getContentType().equals(materialType.getValue()))
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

    public void addQuizCount(int submittedQuizCount) {
        this.stats.setTotalSolvedCount(stats.getTotalSolvedCount() + submittedQuizCount);
    }

    public void addCorrectQuizCount(int submittedQuizRequestStream) {
        this.stats.setTotalCorrectCount(stats.getTotalCorrectCount() + submittedQuizRequestStream);
    }
}
