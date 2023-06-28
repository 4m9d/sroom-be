package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "재생목록 세부 정보")
@Data
public class PlaylistDetail {

    private String lectureId;
    private String lecturedTitle;
    private String channel;
    private String description;
    private boolean isPlaylist;
    private String publishedAt;
    private int lectureCount;
    private double rating;
    private int reviewCount;
    private String thumbnail;
    private List<Index> indexes;
    private List<ReviewBrief> reviews;

    @Builder
    public PlaylistDetail(String lectureId, String lecturedTitle, String channel, String description, boolean isPlaylist, String publishedAt, int lectureCount, double rating, int reviewCount, String thumbnail, List<Index> indexes, List<ReviewBrief> reviews) {
        this.lectureId = lectureId;
        this.lecturedTitle = lecturedTitle;
        this.channel = channel;
        this.description = description;
        this.isPlaylist = isPlaylist;
        this.publishedAt = publishedAt;
        this.lectureCount = lectureCount;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.thumbnail = thumbnail;
        this.indexes = indexes;
        this.reviews = reviews;
    }
}
