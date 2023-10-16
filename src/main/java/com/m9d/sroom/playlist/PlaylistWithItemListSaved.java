package com.m9d.sroom.playlist;

import com.m9d.sroom.playlist.repository.PlaylistSaved;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.video.PlaylistItemSaved;
import lombok.Getter;

import java.util.List;

import static com.m9d.sroom.course.constant.CourseConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS;

public class PlaylistWithItemListSaved extends PlaylistSaved {

    @Getter
    private final List<PlaylistItemSaved> playlistItemSavedList;

    public PlaylistWithItemListSaved(PlaylistDto playlistDto, List<PlaylistItemSaved> playlistItemSavedList) {
        super(playlistDto);
        this.playlistItemSavedList = playlistItemSavedList;
    }

    @Override
    public boolean isPlaylist() {
        return true;
    }
}
