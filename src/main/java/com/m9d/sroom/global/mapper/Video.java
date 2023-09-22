package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class Video {

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

    private int index;

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

    public static RowMapper<Video> getRowMapper() {
        return (rs, rowNum) -> Video.builder()
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

}
