package com.m9d.sroom.playlist.vo;

import com.m9d.sroom.video.vo.PlaylistItem;
import lombok.Getter;

import java.util.List;

@Getter
public class PlaylistWithItemList extends Playlist {

    private final List<PlaylistItem> playlistItemList;

    public PlaylistWithItemList(Playlist playlist, List<PlaylistItem> playlistItemList) {
        super(playlist.getCode(),
                playlist.getTitle(),
                playlist.getChannel(),
                playlist.getThumbnail(),
                playlist.getDescription(),
                playlist.getPublishedAt(),
                playlist.getVideoCount(),
                playlist.getReviewCount(),
                playlist.getRating());
        this.playlistItemList = playlistItemList;
    }

    public int getPlaylistDuration() {
        return playlistItemList.stream()
                .mapToInt(PlaylistItem::getDuration)
                .sum();
    }
}
