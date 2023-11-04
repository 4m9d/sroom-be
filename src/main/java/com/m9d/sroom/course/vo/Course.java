package com.m9d.sroom.course.vo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private final Timestamp lastViewTime;

    private final List<CourseVideo> courseVideoList;

    public Course(String title, int duration, String thumbnail, Boolean scheduled, Date expectedEndDate, Integer weeks,
                  Integer dailyTargetTime, Date startDate, Timestamp lastViewTime, List<CourseVideo> courseVideoList) {
        this.title = title;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.scheduled = scheduled;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.weeks = weeks;
        this.dailyTargetTime = dailyTargetTime;
        this.lastViewTime = lastViewTime;
        this.courseVideoList = courseVideoList;
    }

    public static Course createNoScheduled(String title, int duration, String thumbnail, List<CourseVideo> courseVideoList){
        return new Course(title, duration, thumbnail, false, null, null,
                null, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), courseVideoList);
    }

    public int getLastLectureIndex() {
        return courseVideoList.stream()
                .mapToInt(CourseVideo::getLectureIndex)
                .max()
                .orElse(0);
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

    public void addCourseDuration(int secondToAdd) {
        this.duration += secondToAdd;
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

    public boolean hasUnpreparedVideo(){
        return courseVideoList.stream().anyMatch(video -> video.getSummaryId() == 0);
    }
}
