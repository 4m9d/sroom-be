package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.lecture.dto.VideoCompletionStatus;
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import lombok.Getter;
import lombok.Setter;

import static com.m9d.sroom.lecture.constant.LectureConstant.LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS;
import static com.m9d.sroom.lecture.constant.LectureConstant.MINIMUM_VIEW_PERCENT_FOR_COMPLETION;

@Getter
public class CourseVideo {

    private final Long videoId;

    private final Long summaryId;

    @Setter
    private int section;

    private final int videoIndex;

    private final int lectureIndex;

    private final boolean isComplete;

    private final int maxDuration;

    public CourseVideo(Long videoId, Long summaryId, int section, int videoIndex, int lectureIndex) {
        this.videoId = videoId;
        this.section = section;
        this.summaryId = summaryId;
        this.videoIndex = videoIndex;
        this.lectureIndex = lectureIndex;
        this.isComplete = false;
        this.maxDuration = 0;
    }

    public CourseVideo(Long videoId, Long summaryId, int section, int videoIndex, int lectureIndex, boolean isComplete, int maxDuration) {
        this.videoId = videoId;
        this.summaryId = summaryId;
        this.section = section;
        this.videoIndex = videoIndex;
        this.lectureIndex = lectureIndex;
        this.isComplete = isComplete;
        this.maxDuration = maxDuration;
    }

    public VideoCompletionStatus getCompletionStatus(int viewDuration, int videoDuration, boolean isMarkedAsCompleted) {
        VideoCompletionStatus status = new VideoCompletionStatus();
        status.setRewound(viewDuration - maxDuration <= 0);
        status.setCompletedNow(false);

        if (isComplete) {
            status.setCompleted(true);
        } else {
            status.setCompleted(false);

            boolean currVideoComplete =
                    (viewDuration / (double) videoDuration) > MINIMUM_VIEW_PERCENT_FOR_COMPLETION
                            || isMarkedAsCompleted;
            if (currVideoComplete) {
                status.setCompleted(true);
                status.setCompletedNow(true);
            }
        }

        if (viewDuration >= videoDuration - LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS) {
            status.setFullyWatched(true);
            status.setCompleted(true);
        } else {
            status.setFullyWatched(false);
        }

        status.setTimeGap(videoDuration - viewDuration);

        return status;
    }
}
