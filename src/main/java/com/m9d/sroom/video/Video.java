package com.m9d.sroom.video;

import com.m9d.sroom.common.object.Content;
import com.m9d.sroom.youtube.dto.VideoInfo;

import java.sql.Timestamp;

public class Video extends Content {

    private final VideoInfo videoInfo;

    public Video(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @Override
    public String getTitle() {
        return videoInfo.getTitle();
    }

    @Override
    public int getDuration() {
        return videoInfo.getDuration();
    }

    @Override
    public String getThumbnail() {
        return videoInfo.getThumbnail();
    }

    @Override
    public String getChannel() {
        return videoInfo.getChannel();
    }

    @Override
    public boolean isPlaylist() {
        return false;
    }

    @Override
    public String getDescription() {
        return videoInfo.getDescription();
    }

    @Override
    public String getCode() {
        return videoInfo.getCode();
    }

    @Override
    public Timestamp getPublishedAt() {
        return videoInfo.getPublishedAt();
    }


    public String getLanguage() {
        return videoInfo.getLanguage();
    }

    public Long getViewCount() {
        return videoInfo.getViewCount();
    }

    public boolean getMembership() {
        return videoInfo.getMembership();
    }

    public PlaylistItem toPlaylistItem(Integer position) {
        return new PlaylistItem(videoInfo, position);
    }

    public String getLicense() {
        return videoInfo.getLicense();
    }
}
