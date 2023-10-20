package com.m9d.sroom.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Getter
public class Video extends Content {

    private final String code;

    private final String title;

    private final String channel;

    private final String thumbnail;

    private final String description;

    private final Integer duration;

    private final Long viewCount;

    private final Timestamp publishedAt;

    private final String language;

    private final String license;

    private final Boolean membership;

    @Override
    public Integer getVideoCount() {
        return 1;
    }

    @Override
    public Boolean isPlaylist() {
        return false;
    }


}
