package com.m9d.sroom.playlist;

import com.m9d.sroom.common.object.Content;
import com.m9d.sroom.youtube.dto.PlaylistInfo;
import lombok.Getter;

import java.sql.Timestamp;

public class Playlist extends Content {

    private final PlaylistInfo playlistInfo;

    public Playlist(PlaylistInfo playlistInfo) {
        this.playlistInfo = playlistInfo;
    }

    protected PlaylistInfo getPlaylistInfo(){
        return playlistInfo;
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

    @Override
    public String getDescription() {
        return playlistInfo.getDescription();
    }

    @Override
    public String getCode() {
        return playlistInfo.getCode();
    }

    @Override
    public Timestamp getPublishedAt() {
        return playlistInfo.getPublishedAt();
    }

    public int getVideoCount() {
        return playlistInfo.getVideoCount();
    }
}
