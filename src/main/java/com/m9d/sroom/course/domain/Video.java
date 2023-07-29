package com.m9d.sroom.course.domain;

import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
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

    private long viewCount;

    private String publishedAt;

    private double rating;

    private int reviewCount;

    private String thumbnail;

    private int index;
}
