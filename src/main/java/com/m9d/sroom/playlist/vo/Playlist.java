package com.m9d.sroom.playlist.vo;

import com.m9d.sroom.common.vo.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@Getter
public class Playlist extends Content {

    private final String code;

    private final String title;

    private final String channel;

    private final String thumbnail;

    private final String description;

    private final Timestamp publishedAt;

    private final Integer videoCount;

    @Override
    public Long getViewCount() {
        return -1L;
    }

    @Override
    public Boolean isPlaylist() {
        return true;
    }
}
