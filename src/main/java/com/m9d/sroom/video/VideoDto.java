package com.m9d.sroom.video;

import com.m9d.sroom.youtube.dto.VideoInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class VideoDto {

    private String videoCode;

    private Long videoId;

    private String title;

    private String channel;

    private String description;

    private int duration;

    private boolean enrolled;

    private boolean playlist;

    private Long viewCount;

    private Timestamp publishedAt;

    private int accumulatedRating;

    private double rating;

    private int reviewCount;

    private String thumbnail;

    private Integer index;

    private String language;

    private String license;

    private Timestamp updatedAt;

    private boolean complete;

    private boolean usable;

    private boolean membership;

    private Long summaryId;

    private boolean available;

    private boolean chapterUse;

    private Integer materialStatus;

    public VideoDto(Video video) {
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
        this.summaryId = 0L;
    }

    public VideoInfo toVideoInfo() {
        return VideoInfo.builder()
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

    public static RowMapper<VideoDto> getRowMapper() {
        return (rs, rowNum) -> VideoDto.builder()
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
                .build();
    }

    public static RowMapper<VideoDto> getRowMapperWithIndex() {
        return (rs, rowNum) -> VideoDto.builder()
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
                .index(rs.getInt("video_index"))
                .build();
    }

}
