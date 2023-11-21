package com.m9d.sroom.course.vo;

import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.search.dto.VideoCompletionStatus;
import com.m9d.sroom.material.model.MaterialStatus;
import lombok.Getter;
import lombok.Setter;

import static com.m9d.sroom.search.constant.SearchConstant.LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS;
import static com.m9d.sroom.search.constant.SearchConstant.MINIMUM_VIEW_PERCENT_FOR_COMPLETION;

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

    public int getProgress(int videoDuration) {
        if (videoDuration - CourseConstant.VIDEO_THRESHOLD_SECONDS_BEFORE_END < maxDuration) {
            return 100;
        }

        return (int) (((long) maxDuration * 100) / videoDuration);
    }

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

        if (isMarkedAsCompleted) {
            status.setTimeGap(videoDuration - maxDuration);
            status.setViewDuration(videoDuration - 1);
            status.setRewound(false);
        } else {
            status.setTimeGap(Math.max(viewDuration - maxDuration, 0));
            status.setViewDuration(viewDuration);
        }

        return status;
    }

    public MaterialStatus getMaterialStatus() {
        return MaterialStatus.from(summaryId);
    }
}
