package com.m9d.sroom.review;

import com.m9d.sroom.common.entity.jpa.ReviewEntity;
import com.m9d.sroom.search.dto.response.ReviewBrief;

import java.text.SimpleDateFormat;

public class ReviewMapper {

    public static ReviewBrief getBriefByEntity(ReviewEntity review, int index) {
        return ReviewBrief.builder()
                .index(index)
                .reviewContent(review.getContent())
                .submittedRating(review.getSubmittedRating())
                .reviewerName(review.getMember().getMemberName())
                .publishedAt(new SimpleDateFormat("yyyy-MM-dd")
                        .format(review.getSubmittedDate()))
                .build();
    }
}
