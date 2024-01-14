package com.m9d.sroom.playlist.vo;

import com.m9d.sroom.common.entity.jpa.embedded.Review;
import com.m9d.sroom.common.vo.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.DecimalFormat;

@Builder
@AllArgsConstructor
@Getter
public class Playlist extends Content {

    private final String code;

    private final String title;

    private final String channel;

    private final String thumbnail;

    private final String description;

    private final Timestamp publishedAt;

    private final Integer videoCount;

    private Integer reviewCount;

    private Double rating;

    public void setReviewInfo(Review review) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        this.reviewCount = review.getReviewCount();
        this.rating = Double.parseDouble(decimalFormat.format((double) review.getAccumulatedRating()
                / reviewCount));
    }

    @Override
    public Long getViewCount() {
        return -1L;
    }

    @Override
    public Boolean isPlaylist() {
        return true;
    }
}
