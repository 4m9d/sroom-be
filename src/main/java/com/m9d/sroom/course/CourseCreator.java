package com.m9d.sroom.course;

import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.course.vo.CourseVideo;
import com.m9d.sroom.util.DateUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseCreator {

    public Course create(NewLecture newLecture, boolean useSchedule, EnrollContentInfo contentInfo) {
        if (!useSchedule) {
            return Course.createNoScheduled(contentInfo.getTitle(), contentInfo.getTotalContentDuration(),
                    contentInfo.getThumbnail(), createCourseVideoList(contentInfo, false,
                            newLecture.getScheduling()));
        } else {
            return new Course(contentInfo.getTitle(), contentInfo.getTotalContentDuration(), contentInfo.getThumbnail(),
                    true, DateUtil.convertStringToDate(newLecture.getExpectedEndDate()),
                    newLecture.getScheduling().size(), newLecture.getDailyTargetTime()
                    , new Timestamp(System.currentTimeMillis()),
                    createCourseVideoList(contentInfo, true, newLecture.getScheduling()));
        }
    }

    private List<CourseVideo> createCourseVideoList(EnrollContentInfo contentInfo, boolean useSchedule,
                                                    List<Integer> scheduling) {
        if (contentInfo.isPlaylist()) {
            return createCourseVideoListWithPlaylist(contentInfo.getInnerContentList(), useSchedule, scheduling);
        } else {
            return createCourseVideoWithVideo(contentInfo.getInnerContentList().get(0));
        }
    }

    private ArrayList<CourseVideo> createCourseVideoWithVideo(InnerContent innerContent) {
        return new ArrayList<>(List.of(new CourseVideo(innerContent.getContentId(), innerContent.getSummaryId(),
                CourseConstant.ENROLL_DEFAULT_SECTION_NO_SCHEDULE, CourseConstant.ENROLL_VIDEO_INDEX,
                CourseConstant.ENROLL_LECTURE_INDEX)));
    }

    private List<CourseVideo> createCourseVideoListWithPlaylist(List<InnerContent> contentList, boolean useSchedule,
                                                                List<Integer> scheduling) {
        List<CourseVideo> courseVideoList = new ArrayList<>();
        int videoCount = 1;
        int week = 0;
        int videoIndex = 1;
        int section = useSchedule ? CourseConstant.ENROLL_DEFAULT_SECTION_SCHEDULE : CourseConstant.ENROLL_DEFAULT_SECTION_NO_SCHEDULE;


        for (InnerContent content : contentList) {
            if (useSchedule && videoCount > scheduling.get(week)) {
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
