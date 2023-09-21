package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
@Builder
public class Video {

    private String videoCode;

    private Long videoId;

    private String title;

    private String channel;

    private String description;

    private int duration;

    private boolean enrolled;

    private boolean playlist;

    private Long viewCount;

    private Timestamp publishedAt;

    private int accumulatedRating;

    private double rating;

    private int reviewCount;

    private String thumbnail;

    private int index;

    private String language;

    private String license;

    private Timestamp updatedAt;

    private boolean complete;

    private boolean usable;

    private boolean membership;

    private Long summaryId;

    private boolean available;

    private boolean chapterUse;

    private Integer materialStatus;

}
