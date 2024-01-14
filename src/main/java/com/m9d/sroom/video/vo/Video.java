package com.m9d.sroom.video.vo;

import com.m9d.sroom.common.entity.jpa.embedded.Review;
import com.m9d.sroom.common.vo.Content;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.dto.video.VideoDto;
import com.m9d.sroom.youtube.dto.video.VideoItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import static com.m9d.sroom.youtube.YoutubeConstant.*;

@AllArgsConstructor
@Builder
@Getter
public class Video extends Content {

    private final String code;

    private final String title;

    private final String channel;

    private final String thumbnail;

    private final String description;

    private final Integer duration;

    private final Long viewCount;

    private final Timestamp publishedAt;

    private final String language;

    private final String license;

    private final Boolean membership;

    private Integer reviewCount;

    private Double rating;

    public void setReviewInfo(Review review) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        this.reviewCount = review.getReviewCount();
        this.rating = Double.parseDouble(decimalFormat.format((double) review.getAccumulatedRating()
                / reviewCount));
    }

    @Override
    public Integer getVideoCount() {
        return 1;
    }

    @Override
    public Boolean isPlaylist() {
        return false;
    }
}
