package com.m9d.sroom.playlist;

import com.m9d.sroom.common.object.ContentSaved;

public class PlaylistSaved extends ContentSaved {


    private final PlaylistDto playlistDto;
    public PlaylistSaved(PlaylistDto playlistDto) {
        this.playlistDto = playlistDto;
    }

    protected PlaylistDto getPlaylistDto() {
        return playlistDto;
    }

    @Override
    public String getTitle() {
        return playlistDto.getTitle();
    }

    @Override
    public int getDuration() {
        return playlistDto.getDuration();
    }

    @Override
    public String getThumbnail() {
        return playlistDto.getThumbnail();
    }

    @Override
    public String getChannel() {
        return playlistDto.getChannel();
    }

    @Override
    protected Long getId() {
        return playlistDto.getPlaylistId();
    }

    @Override
    public boolean isPlaylist() {
        return true;
    }
}
