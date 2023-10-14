package com.m9d.sroom.video;

import com.m9d.sroom.youtube.dto.VideoInfo;
import lombok.Getter;

@Getter
public class PlaylistItem extends Video {

    private final Integer index;

    public PlaylistItem(VideoInfo videoInfo, Integer index) {
        super(videoInfo);
        this.index = index;
    }
}
