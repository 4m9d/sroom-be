package com.m9d.sroom.video.vo;

import lombok.Getter;

@Getter
public class PlaylistItem extends Video {

    private final Integer index;

    public PlaylistItem(Video video, Integer index) {
        super(video.getCode(),
                video.getTitle(),
                video.getChannel(),
                video.getThumbnail(),
                video.getDescription(),
                video.getDuration(),
                video.getViewCount(),
                video.getPublishedAt(),
                video.getLanguage(),
                video.getLicense(),
                video.getMembership(),
                video.getReviewCount(),
                video.getRating());
        this.index = index;
    }
}
