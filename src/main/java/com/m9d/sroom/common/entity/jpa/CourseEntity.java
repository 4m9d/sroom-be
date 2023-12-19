package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Scheduling;
import com.m9d.sroom.course.vo.CourseVideo;
import com.m9d.sroom.search.dto.response.VideoWatchInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "COURSE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private String courseTitle;

    private Integer courseDuration;

    @UpdateTimestamp
    private Timestamp lastViewTime;

    private Integer progress;

    @Column(columnDefinition = "text")
    private String thumbnail;

    @Embedded
    private Scheduling scheduling;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @OneToMany(mappedBy = "course")
    private List<CourseVideoEntity> courseVideos = new ArrayList<CourseVideoEntity>();

    @OneToMany(mappedBy = "course")
    private List<LectureEntity> lectures = new ArrayList<LectureEntity>();

    @OneToMany(mappedBy = "course")
    private List<CourseDailyLogEntity> dailyLogs = new ArrayList<CourseDailyLogEntity>();

    private CourseEntity(MemberEntity member, String courseTitle, String thumbnail, Scheduling scheduling) {
        this.courseTitle = courseTitle;
        this.courseDuration = 0;
        this.lastViewTime = new Timestamp(System.currentTimeMillis());
        this.progress = 0;
        this.thumbnail = thumbnail;
        this.scheduling = scheduling;
        this.startDate = new Date();
        setMember(member);
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            this.member.getCourses().remove(this);
        }
        this.member = member;
        member.getCourses().add(this);
    }

    public static CourseEntity createWithoutSchedule(MemberEntity member, String courseTitle, String thumbnail) {
        return new CourseEntity(member, courseTitle, thumbnail,
                new Scheduling(false, null, null, null));
    }

    public static CourseEntity createWithSchedule(MemberEntity member, String courseTitle, String thumbnail,
                                                  boolean isScheduled, int weeks, Date expectedEndDate,
                                                  int dailyTargetTime) {
        return new CourseEntity(member, courseTitle, thumbnail, new Scheduling(isScheduled, weeks, expectedEndDate,
                dailyTargetTime));
    }

    public Boolean isCompleted() {
        return progress.equals(100);
    }


    public Optional<CourseDailyLogEntity> findDailyLogByDate(Date date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);

        return dailyLogs.stream()
                .filter(dailyLog -> {
                    cal2.setTime(dailyLog.getDailyLogDate());
                    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
                })
                .findFirst();
    }

    public CourseVideoEntity getCourseVideoByIndex(int videoIndex) {
        return courseVideos.stream()
                .filter(v -> v.getSequence().getVideoIndex() == videoIndex)
                .findFirst()
                .orElse(null);
    }

    public List<CourseVideoEntity> getCourseVideoListOrderByIndex() {
        return courseVideos.stream()
                .sorted(Comparator.comparingInt(v -> v.getSequence().getVideoIndex()))
                .collect(Collectors.toList());
    }

    public Optional<CourseVideoEntity> getCourseVideoByPrevIndex(int videoIndex) {
        return courseVideos.stream()
                .filter(v -> v.getSequence().getVideoIndex() > videoIndex)
                .min(Comparator.comparingInt(v -> v.getSequence().getVideoIndex()));
    }

    public CourseVideoEntity getLastCourseVideo() {
        List<CourseVideoEntity> courseVideosViewed = courseVideos.stream()
                .filter(courseVideo -> courseVideo.getStatus().getLastViewTime() != null)
                .collect(Collectors.toList());

        if (!courseVideosViewed.isEmpty()) {
            return courseVideosViewed.stream()
                    .max(Comparator.comparing(courseVideo -> courseVideo.getStatus().getLastViewTime()))
                    .get();
        } else {
            return courseVideos.stream()
                    .min(Comparator.comparing(courseVideo -> courseVideo.getSequence().getVideoIndex()))
                    .get();
        }
    }

    public List<CourseVideoEntity> getCourseVideoBySection(int section) {
        return courseVideos.stream()
                .filter(v -> v.getSequence().getSection() == section)
                .sorted(Comparator.comparingInt(v -> v.getSequence().getVideoIndex()))
                .collect(Collectors.toList());
    }

    public int countCompletedVideo() {
        return (int) courseVideos.stream()
                .filter(courseVideo -> courseVideo.getStatus().getIsComplete())
                .count();
    }

    public List<VideoWatchInfo> getWatchInfoListBySection(int section) {
        return courseVideos.stream()
                .filter(courseVideo -> courseVideo.getSequence().getSection() == section)
                .map(CourseVideoEntity::toWatchInfo)
                .collect(Collectors.toList());
    }

    public HashSet<String> getLectureChannelSet() {
        return lectures.stream()
                .map(LectureEntity::getChannel)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
