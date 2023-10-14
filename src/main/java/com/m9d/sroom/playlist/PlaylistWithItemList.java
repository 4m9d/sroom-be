package com.m9d.sroom.playlist;

import com.m9d.sroom.youtube.dto.PlaylistInfo;
import com.m9d.sroom.video.PlaylistItem;
import lombok.Getter;

import java.util.List;

public class PlaylistWithItemList extends Playlist{

    @Getter
    private final List<PlaylistItem> playlistItemList;

    public PlaylistWithItemList(PlaylistInfo playlistInfo, List<PlaylistItem> playlistItemList) {
        super(playlistInfo);
        this.playlistItemList = playlistItemList;
    }
}
