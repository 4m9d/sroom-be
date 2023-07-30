package com.m9d.sroom.course.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter @Setter
@Builder
public class Playlist {

    private String playlistCode;

    private Long playlistId;

    private String title;

    private String channel;

    private int duration;

    private boolean enrolled;

    private String description;

    private Timestamp publishedAt;

    private int lectureCount;

    private double rating;

    private int reviewCount;

    private String thumbnail;

    private Timestamp updatedAt;

    private List<Video> videoList;
}
