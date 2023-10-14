package com.m9d.sroom.object;

import com.m9d.sroom.util.youtube.dto.PlaylistInfo;
import lombok.Getter;

import java.util.List;

public class PlaylistWithVideoList extends Playlist{

    @Getter
    private final List<PlaylistItem> playlistItemList;

    public PlaylistWithVideoList(PlaylistInfo playlistInfo, List<PlaylistItem> playlistItemList) {
        super(playlistInfo);
        this.playlistItemList = playlistItemList;
    }
}
