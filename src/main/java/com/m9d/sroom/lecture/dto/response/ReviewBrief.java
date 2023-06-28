package com.m9d.sroom.lecture.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ReviewBrief {
    private int index;
    private String reviewContent;
    private int submittedRating;

    @Builder
    public ReviewBrief(int index, String reviewContent, int submittedRating) {
        this.index = index;
        this.reviewContent = reviewContent;
        this.submittedRating = submittedRating;
    }
}
