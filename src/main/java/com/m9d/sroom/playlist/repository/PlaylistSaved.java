package com.m9d.sroom.playlist.repository;

import com.m9d.sroom.common.object.ContentSaved;
import com.m9d.sroom.playlist.PlaylistDto;
import com.m9d.sroom.util.DateUtil;

import java.sql.Timestamp;

import static com.m9d.sroom.course.constant.CourseConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS;

public class PlaylistSaved extends ContentSaved {

    protected final PlaylistDto playlistDto;

    public PlaylistSaved(PlaylistDto playlistDto) {
        this.playlistDto = playlistDto;
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
    public boolean isPlaylist() {
        return false;
    }

    @Override
    public String getDescription() {
        return playlistDto.getDescription();
    }

    @Override
    public String getCode() {
        return playlistDto.getPlaylistCode();
    }

    @Override
    public Timestamp getPublishedAt() {
        return playlistDto.getPublishedAt();
    }

    @Override
    public Long getId() {
        return playlistDto.getPlaylistId();
    }

    @Override
    public boolean isRecentContent() {
        return DateUtil.hasRecentUpdate(playlistDto.getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS);
    }
}
