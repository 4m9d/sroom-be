package com.m9d.sroom.course.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Playlist {

    private String playlistCode;

    private Long playlistId;

    private String title;

    private String channel;

    private int duration;

    private boolean enrolled;

    private String description;

    private boolean playlist;

    private String publishedAt;

    private int lectureCount;

    private double rating;

    private int reviewCount;

    private String thumbnail;
}
