package com.m9d.sroom.lecture.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Lecture{

    private String lectureTitle;
    private String description;
    private String lectureId;
    private boolean isPlaylist;
    private double rating;
    private int reviewCount;
    private String thumbnail;

    @Builder
    public Lecture(String lectureTitle, String description, String lectureId, boolean isPlaylist, double rating, int reviewCount, String thumbnail) {
        this.lectureTitle = lectureTitle;
        this.description = description;
        this.lectureId = lectureId;
        this.isPlaylist = isPlaylist;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.thumbnail = thumbnail;
    }
}
