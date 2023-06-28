package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "동영상 상세 정보")
@Data
public class VideoDetail {

    private String lectureId;
    private String lectureTitle;
    private String channel;
    private String description;
    @JsonProperty("isPlaylist")
    private boolean isPlaylist;
    private long viewCount;
    private String publishedAt;
    private double rating;
    private int reviewCount;
    private String thumbnail;
    private List<ReviewBrief> reviews;

    @Builder
    public VideoDetail(String lectureId, String lectureTitle, String channel, String description, boolean isPlaylist, long viewCount, String publishedAt, double rating, int reviewCount, String thumbnail, List<ReviewBrief> reviews) {
        this.lectureId = lectureId;
        this.lectureTitle = lectureTitle;
        this.channel = channel;
        this.description = description;
        this.isPlaylist = isPlaylist;
        this.viewCount = viewCount;
        this.publishedAt = publishedAt;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.thumbnail = thumbnail;
        this.reviews = reviews;
    }
}
