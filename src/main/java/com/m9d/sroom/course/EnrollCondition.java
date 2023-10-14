package com.m9d.sroom.course;

import com.m9d.sroom.common.object.CourseVideo;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.common.object.ContentSaved;
import com.m9d.sroom.video.PlaylistItemSaved;
import com.m9d.sroom.playlist.PlaylistSaved;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.video.VideoSaved;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.course.constant.CourseConstant.ENROLL_LECTURE_INDEX;

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

    public Course enrollCourse(ContentSaved contentSaved) {
        return new Course(this, contentSaved, createCourseVideoList(contentSaved));
    }

    private List<CourseVideo> createCourseVideoList(ContentSaved contentSaved) {
        int section = ENROLL_DEFAULT_SECTION_NO_SCHEDULE;
        if (scheduled) {
            section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        }

        List<CourseVideo> courseVideoList = new ArrayList<>();
        if (contentSaved instanceof VideoSaved) {
            courseVideoList.add(new CourseVideo((VideoSaved) contentSaved, section, ENROLL_VIDEO_INDEX, ENROLL_LECTURE_INDEX));
        } else if (contentSaved instanceof PlaylistSaved) {
            int videoCount = 1;
            int week = 0;
            int videoIndex = 1;

            PlaylistSaved playlistSaved = (PlaylistSaved) contentSaved;
            for (PlaylistItemSaved playlistItemSaved : playlistSaved.getPlaylistItemSavedList()) {
                if (scheduled && videoCount > scheduling.get(week)) {
                    week++;
                    section++;
                    videoCount = 1;
                }
                courseVideoList.add(new CourseVideo(playlistItemSaved, section, videoIndex++, ENROLL_LECTURE_INDEX));
                videoCount++;
            }
        } else {
            return null;
        }
        return courseVideoList;
    }
}
