package com.m9d.sroom.video;

public class PlaylistItemSaved extends VideoSaved {

    private final Integer index;

    public PlaylistItemSaved(VideoDto videoDto, Integer index) {
        super(videoDto);
        this.index = index;
    }
}
