package com.m9d.sroom.video;

import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.video.vo.Video;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class VideoMapper {
    static Video getVoByEntity(VideoEntity videoEntity) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        return Video.builder()
                .code(videoEntity.getVideoCode())
                .title(videoEntity.getContentInfo().getTitle())
                .channel(videoEntity.getContentInfo().getChannel())
                .thumbnail(videoEntity.getContentInfo().getThumbnail())
                .description(videoEntity.getContentInfo().getDescription())
                .viewCount(videoEntity.getViewCount())
                .publishedAt(videoEntity.getContentInfo().getPublishedAt())
                .language(videoEntity.getLanguage())
                .license(videoEntity.getLicense())
                .membership(videoEntity.getMembership())
                .reviewCount(videoEntity.getReview().getReviewCount())
                .rating(Double.parseDouble(decimalFormat.format((double)
                        videoEntity.getReview().getAccumulatedRating() / videoEntity.getReview().getReviewCount())))
                .build();
    }

    public EnrollContentInfo getEnrollContentInfo(VideoEntity videoEntity) {
        List<InnerContent> innerContentList = new ArrayList<>(List.of(new InnerContent(videoEntity.getVideoId(),
                videoEntity.getSummaryId(), videoEntity.getContentInfo().getDuration())));

        return EnrollContentInfo.builder()
                .isPlaylist(false)
                .contentId(videoEntity.getVideoId())
                .title(videoEntity.getContentInfo().getTitle())
                .totalContentDuration(videoEntity.getContentInfo().getDuration())
                .thumbnail(videoEntity.getContentInfo().getThumbnail())
                .channel(videoEntity.getContentInfo().getChannel())
                .innerContentList(innerContentList)
                .build();
    }
}
