package com.m9d.sroom.course;

import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.util.DateUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class EnrollCondition {

    private final Boolean scheduled;

    private final Date expectedEndDate;

    private final List<Integer> scheduling;

    private final Integer weeks;

    private final Integer dailyTargetTime;

    public EnrollCondition(NewLecture newLecture, Boolean scheduled) {
        this.scheduled = scheduled;
        if (scheduled) {
            this.expectedEndDate = DateUtil.convertStringToDate(newLecture.getExpectedEndDate());
            this.scheduling = newLecture.getScheduling();
            this.weeks = newLecture.getScheduling().size();
            this.dailyTargetTime = newLecture.getDailyTargetTime();
        } else {
            this.expectedEndDate = null;
            this.scheduling = null;
            this.weeks = null;
            this.dailyTargetTime = null;
        }
    }

    public Course createCourse(EnrollContentInfo contentInfo) {
        return new Course(this, contentInfo, createCourseVideoList(contentInfo));
    }

    private List<CourseVideo> createCourseVideoList(EnrollContentInfo contentInfo) {
        if (contentInfo.isPlaylist()) {
            return createCourseVideoListWithPlaylist(contentInfo.getInnerContentList());
        } else {
            return createCourseVideoWithVideo(contentInfo.getInnerContentList().get(0));
        }
    }

    private ArrayList<CourseVideo> createCourseVideoWithVideo(InnerContent innerContent) {
        return new ArrayList<>(List.of(new CourseVideo(innerContent.getContentId(), innerContent.getSummaryId(),
                CourseConstant.ENROLL_DEFAULT_SECTION_NO_SCHEDULE, CourseConstant.ENROLL_VIDEO_INDEX,
                CourseConstant.ENROLL_LECTURE_INDEX)));
    }

    private List<CourseVideo> createCourseVideoListWithPlaylist(List<InnerContent> contentList) {
        List<CourseVideo> courseVideoList = new ArrayList<>();
        int videoCount = 1;
        int week = 0;
        int videoIndex = 1;
        int section = scheduled ? CourseConstant.ENROLL_DEFAULT_SECTION_SCHEDULE : CourseConstant.ENROLL_DEFAULT_SECTION_NO_SCHEDULE;


        for (InnerContent content : contentList) {
            if (scheduled && videoCount > scheduling.get(week)) {
                week++;
                section++;
                videoCount = 1;
            }
            courseVideoList.add(new CourseVideo(content.getContentId(), content.getSummaryId(), section, videoIndex++,
                    CourseConstant.ENROLL_LECTURE_INDEX));
            videoCount++;
        }
        return courseVideoList;
    }
}
