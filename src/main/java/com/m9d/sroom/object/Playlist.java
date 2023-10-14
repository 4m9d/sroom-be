package com.m9d.sroom.object;

import com.m9d.sroom.util.youtube.dto.PlaylistInfo;

public class Playlist extends Content {

    private final PlaylistInfo playlistInfo;

    public Playlist(PlaylistInfo playlistInfo) {
        this.playlistInfo = playlistInfo;
    }

    @Override
    public String getTitle() {
        return playlistInfo.getTitle();
    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public String getThumbnail() {
        return playlistInfo.getThumbnail();
    }

    @Override
    public String getChannel() {
        return playlistInfo.getChannel();
    }

    @Override
    public boolean isPlaylist() {
        return true;
    }
}
