package com.m9d.sroom.object;

import com.m9d.sroom.global.mapper.VideoDto;
import lombok.Getter;

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
    protected Long getId() {
        return videoDto.getVideoId();
    }

    @Override
    public boolean isPlaylist() {
        return false;
    }

    public Long getVideoId() {
        return videoDto.getVideoId();
    }

    public Long getSummaryId() {
        return videoDto.getSummaryId();
    }
}
