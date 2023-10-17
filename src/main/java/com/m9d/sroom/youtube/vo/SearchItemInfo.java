package com.m9d.sroom.youtube.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Getter
public class SearchItemInfo {

    private final String kind;

    private final boolean isPlaylist;

    private final String code;

    private final String title;

    private final String description;

    private final String channelId;

    private final String channel;

    private final Timestamp publishedAt;

    private final String thumbnail;
}
