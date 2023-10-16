package com.m9d.sroom.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.List;

@Getter @Setter
@Builder
public class Playlist {

    private String playlistCode;

    private Long playlistId;

    private String title;

    private String channel;

    private Integer duration;

    private Boolean enrolled;

    private String description;

    private Timestamp publishedAt;

    private Integer videoCount;

    private Integer accumulatedRating;

    private Double rating;

    private Boolean available;

    private Integer reviewCount;

    private String thumbnail;

    private Timestamp updatedAt;

    private List<Video> videoList;

    public static RowMapper<Playlist> getRowMapper() {
        return (rs, rowNum) -> Playlist.builder()
                .playlistId(rs.getLong("playlist_id"))
                .playlistCode(rs.getString("playlist_code"))
                .channel(rs.getString("channel"))
                .thumbnail(rs.getString("thumbnail"))
                .accumulatedRating(rs.getInt("accumulated_rating"))
                .reviewCount(rs.getInt("review_count"))
                .available(rs.getBoolean("is_available"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .title(rs.getString("title"))
                .publishedAt(rs.getTimestamp("published_at"))
                .videoCount(rs.getInt("video_count"))
                .build();

    }
}
