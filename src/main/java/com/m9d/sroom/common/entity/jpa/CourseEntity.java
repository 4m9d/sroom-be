package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Scheduling;
import com.m9d.sroom.course.constant.CourseConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.ENROLL_DEFAULT_SECTION_SCHEDULE;
import static com.m9d.sroom.search.constant.SearchConstant.LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS;
import static com.m9d.sroom.util.DateUtil.DAYS_IN_WEEK;
import static com.m9d.sroom.util.DateUtil.SECONDS_IN_MINUTE;

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

    @OneToMany(mappedBy = "course")
    private List<CourseQuizEntity> courseQuizzes = new ArrayList<CourseQuizEntity>();

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

    public static CourseEntity createWithSchedule(MemberEntity member, String courseTitle, String thumbnail, int weeks,
                                                  Date expectedEndDate, int dailyTargetTime) {
        return new CourseEntity(member, courseTitle, thumbnail, new Scheduling(true, weeks, expectedEndDate,
                dailyTargetTime));
    }

    public Boolean isCompleted() {
        return progress.equals(100);
    }

    public void addDuration(Integer totalContentDuration) {
        this.courseDuration += totalContentDuration;
    }

    public void updateLastViewTime() {
        this.lastViewTime = new Timestamp(System.currentTimeMillis());
    }

    public int getLastLectureIndex() {
        return lectures.stream()
                .max(Comparator.comparing(LectureEntity::getLectureIndex))
                .map(LectureEntity::getLectureIndex)
                .orElse(0);
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

    public Optional<CourseVideoEntity> findCourseVideoByPrevIndex(int videoIndex) {
        return courseVideos.stream()
                .filter(v -> v.getSequence().getVideoIndex() > videoIndex)
                .min(Comparator.comparingInt(v -> v.getSequence().getVideoIndex()));
    }

    public Optional<CourseVideoEntity> findLastCourseVideo() {
        List<CourseVideoEntity> courseVideosViewed = courseVideos.stream()
                .filter(courseVideo -> courseVideo.getStatus().getLastViewTime() != null)
                .collect(Collectors.toList());

        if (!courseVideosViewed.isEmpty()) {
            return courseVideosViewed.stream()
                    .max(Comparator.comparing(courseVideo -> courseVideo.getStatus().getLastViewTime()));
        } else {
            return courseVideos.stream()
                    .min(Comparator.comparing(courseVideo -> courseVideo.getSequence().getVideoIndex()));
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

    public HashSet<String> getLectureChannelSet() {
        return lectures.stream()
                .map(LectureEntity::getChannel)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public int getSumOfMaxDuration() {
        return courseVideos.stream()
                .mapToInt(courseVideo -> courseVideo.getStatus().getMaxDuration())
                .sum();
    }

    public void reschedule() {
        int weeklyTargetTimeForSecond = scheduling.getDailyTargetTime() * SECONDS_IN_MINUTE * DAYS_IN_WEEK;
        int section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        int currentSectionTime = 0;
        int lastSectionTime = 0;

        for (CourseVideoEntity courseVideoEntity : courseVideos) {
            if (currentSectionTime + (courseVideoEntity.getVideo().getContentInfo().getDuration() / 2)
                    > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += courseVideoEntity.getVideo().getContentInfo().getDuration();
            lastSectionTime = currentSectionTime;
            courseVideoEntity.updateSection(section);
        }
        int lastSectionDays =
                (int) Math.ceil((double) lastSectionTime / scheduling.getDailyTargetTime() * SECONDS_IN_MINUTE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);

        this.scheduling.setWeeks(section);
        this.scheduling.setExpectedEndDate(calendar.getTime());
    }

    public void updateProgress() {
        if (courseVideos.size() == 1) {
            int videoDuration = courseVideos.get(0).getVideo().getContentInfo().getDuration();
            int courseDuration = courseVideos.get(0).getStatus().getMaxDuration();

            if (videoDuration - CourseConstant.VIDEO_THRESHOLD_SECONDS_BEFORE_END < courseDuration) {
                this.progress = 100;
            } else {
                this.progress = (int) ((double) courseDuration / videoDuration * 100);
            }

        } else {
            this.progress = (int) ((double) countCompletedVideo() / courseVideos.size() * 100);
        }
    }

    public void updateLastViewVideoToNext(Integer videoIndex) {
        Optional<CourseVideoEntity> courseVideoOptional = findCourseVideoByPrevIndex(videoIndex);

        courseVideoOptional.ifPresent(courseVideoEntity -> courseVideoEntity.updateLastViewTime(
                Timestamp.valueOf(LocalDateTime.now().plusSeconds(LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS))));
    }

    public boolean hasUnpreparedMaterial() {
        return courseVideos.stream()
                .anyMatch(courseVideoEntity -> courseVideoEntity.getSummary() == null);
    }
}
