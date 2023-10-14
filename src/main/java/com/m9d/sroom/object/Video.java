package com.m9d.sroom.object;

import com.m9d.sroom.util.youtube.dto.VideoInfo;
import lombok.Getter;

@Getter
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

    public String getCode() {
        return videoInfo.getCode();
    }


}
