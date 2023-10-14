package com.m9d.sroom.object;

import com.m9d.sroom.global.mapper.VideoDto;

public class PlaylistItemSaved extends VideoSaved{

    private final Integer index;

    public PlaylistItemSaved(VideoDto videoDto, Integer index) {
        super(videoDto);
        this.index = index;
    }
}
