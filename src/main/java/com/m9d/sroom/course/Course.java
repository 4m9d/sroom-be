package com.m9d.sroom.course;

import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.ENROLL_DEFAULT_SECTION_SCHEDULE;
import static com.m9d.sroom.util.DateUtil.DAYS_IN_WEEK;
import static com.m9d.sroom.util.DateUtil.SECONDS_IN_MINUTE;

@Getter
@Slf4j
public class Course {

    private final String title;

    private int duration;

    private final String thumbnail;

    private final boolean scheduled;

    private Date expectedEndDate;

    private final Date startDate;

    private Integer weeks;

    private final Integer dailyTargetTime;

    @Getter
    private final List<CourseVideo> courseVideoList;


    public Course(EnrollCondition enrollCondition, EnrollContentInfo contentInfo, List<CourseVideo> courseVideoList) {
        this.title = contentInfo.getTitle();
        this.duration = contentInfo.getTotalContentDuration();
        this.thumbnail = contentInfo.getThumbnail();
        this.scheduled = enrollCondition.getScheduled();
        this.weeks = enrollCondition.getWeeks();
        this.startDate = new Timestamp(System.currentTimeMillis());
        this.expectedEndDate = enrollCondition.getExpectedEndDate();
        this.dailyTargetTime = enrollCondition.getDailyTargetTime();
        this.courseVideoList = courseVideoList;
    }

    public Course(String title, int duration, String thumbnail, Boolean scheduled, Date expectedEndDate, Integer weeks,
                  Integer dailyTargetTime, Date startDate, List<CourseVideo> courseVideoList) {
        this.title = title;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.scheduled = scheduled;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.weeks = weeks;
        this.dailyTargetTime = dailyTargetTime;
        this.courseVideoList = courseVideoList;
    }

    public int getLastLectureIndex() {
        return courseVideoList.stream()
                .mapToInt(CourseVideo::getLectureIndex)
                .max()
                .orElse(0);
    }

    public void addCourseVideo(List<InnerContent> innerContentList) {
        int videoIndex = courseVideoList.stream()
                .mapToInt(CourseVideo::getVideoIndex)
                .max()
                .orElse(0) + 1;

        int durationToAdd = 0;
        int lastLectureIndex = getLastLectureIndex();

        for (InnerContent innerContent : innerContentList) {
            courseVideoList.add(new CourseVideo(innerContent.getContentId(), innerContent.getSummaryId(),
                    CourseConstant.ENROLL_DEFAULT_SECTION_NO_SCHEDULE, videoIndex++, lastLectureIndex + 1));
            durationToAdd += innerContent.getDuration();
        }
        addCourseDuration(durationToAdd);
    }

    public void reschedule(Integer[] durationList) {
        int weeklyTargetTimeForSecond = dailyTargetTime * SECONDS_IN_MINUTE * DAYS_IN_WEEK;
        int section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        int currentSectionTime = 0;
        int lastSectionTime = 0;

        for (CourseVideo courseVideo : courseVideoList) {
            if (currentSectionTime + (durationList[courseVideo.getVideoIndex()] / 2) > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += durationList[courseVideo.getVideoIndex()];
            lastSectionTime = currentSectionTime;
            courseVideo.setSection(section);
        }
        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTime * SECONDS_IN_MINUTE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);

        setWeeks(section);
        setExpectedEndDate(calendar.getTime());
    }

    private void setExpectedEndDate(Date expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    private void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    private void addCourseDuration(int secondToAdd) {
        this.duration += secondToAdd;
    }

    public List<CourseVideo> getCourseVideoListByLectureIndex(int lectureIndex) {
        return courseVideoList.stream()
                .filter(video -> video.getLectureIndex() == lectureIndex)
                .collect(Collectors.toList());
    }

    public int getCompletionRatio() {
        int completedVideoCount = (int) courseVideoList.stream()
                .filter(CourseVideo::isComplete)
                .count();
        return (int) ((double) completedVideoCount / courseVideoList.size() * 100);
    }

    public int getSumOfMaxDuration() {
        return courseVideoList.stream()
                .mapToInt(CourseVideo::getMaxDuration)
                .sum();
    }
}
