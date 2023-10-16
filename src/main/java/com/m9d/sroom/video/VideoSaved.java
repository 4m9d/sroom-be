package com.m9d.sroom.video;

import com.m9d.sroom.common.object.ContentSaved;
import com.m9d.sroom.util.DateUtil;
import lombok.Getter;

import java.sql.Timestamp;

import static com.m9d.sroom.course.constant.CourseConstant.VIDEO_UPDATE_THRESHOLD_HOURS;

@Getter
public class VideoSaved extends ContentSaved {

    private final VideoDto videoDto;

    public VideoSaved(VideoDto videoDto) {
        this.videoDto = videoDto;
    }

    @Override
    public String getTitle() {
        return videoDto.getTitle();
    }

    @Override
    public int getDuration() {
        return videoDto.getDuration();
    }

    @Override
    public String getThumbnail() {
        return videoDto.getThumbnail();
    }

    @Override
    public String getChannel() {
        return videoDto.getChannel();
    }

    @Override
    public Long getId() {
        return videoDto.getVideoId();
    }

    @Override
    public boolean isRecentContent() {
        return DateUtil.hasRecentUpdate(videoDto.getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS);
    }

    @Override
    public boolean isPlaylist() {
        return false;
    }

    @Override
    public String getDescription() {
        return videoDto.getDescription();
    }

    @Override
    public String getCode() {
        return videoDto.getVideoCode();
    }

    @Override
    public Timestamp getPublishedAt() {
        return videoDto.getPublishedAt();
    }

    public Long getVideoId() {
        return videoDto.getVideoId();
    }

    public Long getSummaryId() {
        return videoDto.getSummaryId();
    }
}
