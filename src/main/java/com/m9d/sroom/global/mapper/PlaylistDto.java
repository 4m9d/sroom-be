package com.m9d.sroom.global.mapper;

import com.m9d.sroom.util.youtube.dto.PlaylistInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.List;

@Getter @Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class PlaylistDto {

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

    private List<VideoDto> videoDtoList;

    public PlaylistDto(PlaylistInfo playlistInfo, int duration) {
        this.playlistCode = playlistInfo.getCode();
        this.title = playlistInfo.getTitle();
        this.channel = playlistInfo.getChannel();
        this.thumbnail = playlistInfo.getThumbnail();
        this.description = playlistInfo.getDescription();
        this.publishedAt = playlistInfo.getPublishedAt();
        this.videoCount = playlistInfo.getVideoCount();
        this.duration = duration;
    }

    public static RowMapper<PlaylistDto> getRowMapper() {
        return (rs, rowNum) -> PlaylistDto.builder()
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
