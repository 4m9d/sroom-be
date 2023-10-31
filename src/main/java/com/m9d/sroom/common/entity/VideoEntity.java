package com.m9d.sroom.common.entity;

import com.m9d.sroom.video.vo.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class VideoEntity {

    private String videoCode;

    private Long videoId;

    private String title;

    private String channel;

    private String description;

    private int duration;

    private boolean playlist;

    private Long viewCount;

    private Timestamp publishedAt;

    private int accumulatedRating;

    private int reviewCount;

    private String thumbnail;

    private String language;

    private String license;

    private Timestamp updatedAt;

    private boolean membership;

    private Long summaryId;

    private boolean available;

    private boolean chapterUse;

    private Integer materialStatus;

    private Float average_rating;

    public static RowMapper<VideoEntity> getRowMapper() {
        return (rs, rowNum) -> VideoEntity.builder()
                .videoId(rs.getLong("video_id"))
                .videoCode(rs.getString("video_code"))
                .duration(rs.getInt("duration"))
                .channel(rs.getString("channel"))
                .thumbnail(rs.getString("thumbnail"))
                .accumulatedRating(rs.getInt("accumulated_rating"))
                .reviewCount(rs.getInt("review_count"))
                .summaryId(rs.getLong("summary_id"))
                .available(rs.getBoolean("is_available"))
                .description(rs.getString("description"))
                .chapterUse(rs.getBoolean("chapter_usage"))
                .title(rs.getString("title"))
                .language(rs.getString("language"))
                .license(rs.getString("license"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .viewCount(rs.getLong("view_count"))
                .publishedAt(rs.getTimestamp("published_at"))
                .membership(rs.getBoolean("membership"))
                .materialStatus(rs.getInt("material_status"))
                .average_rating(rs.getFloat("average_rating"))
                .build();
    }

    public Video toVideo() {
        return Video.builder()
                .code(videoCode)
                .title(title)
                .channel(channel)
                .thumbnail(thumbnail)
                .description(description)
                .duration(duration)
                .viewCount(viewCount)
                .publishedAt(publishedAt)
                .language(language)
                .license(license)
                .membership(membership)
                .build();
    }

    public VideoEntity(Video video, Long summaryId) {
        this.videoCode = video.getCode();
        this.title = video.getTitle();
        this.channel = video.getChannel();
        this.thumbnail = video.getThumbnail();
        this.description = video.getDescription();
        this.duration = video.getDuration();
        this.viewCount = video.getViewCount();
        this.publishedAt = video.getPublishedAt();
        this.language = video.getLanguage();
        this.license = video.getLicense();
        this.membership = video.getMembership();
        this.summaryId = summaryId;
    }

    public VideoEntity updateByYoutube(Video video, long summaryId) {
        this.title = video.getTitle();
        this.channel = video.getChannel();
        this.thumbnail = video.getThumbnail();
        this.description = video.getDescription();
        this.duration = video.getDuration();
        this.viewCount = video.getViewCount();
        this.publishedAt = video.getPublishedAt();
        this.language = video.getLanguage();
        this.membership = video.getMembership();
        this.summaryId = summaryId;
        return this;
    }
}
