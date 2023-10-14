package com.m9d.sroom.object;

import com.m9d.sroom.global.mapper.PlaylistDto;
import lombok.Getter;

import java.util.List;

public class PlaylistSaved extends ContentSaved{

    private final PlaylistDto playlistDto;

    @Getter
    private final List<PlaylistItemSaved> playlistItemSavedList;

    public PlaylistSaved(PlaylistDto playlistDto, List<PlaylistItemSaved> playlistItemSavedList) {
        this.playlistDto = playlistDto;
        this.playlistItemSavedList = playlistItemSavedList;
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
