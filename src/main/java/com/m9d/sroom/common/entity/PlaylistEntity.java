package com.m9d.sroom.common.entity;

import com.m9d.sroom.playlist.vo.Playlist;
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
public class PlaylistEntity {

    private String playlistCode;

    private Long playlistId;

    private String title;

    private String channel;

    private Integer duration;

    private String description;

    private Timestamp publishedAt;

    private Integer videoCount;

    private Integer accumulatedRating;

    private Boolean available;

    private Integer reviewCount;

    private String thumbnail;

    private Timestamp updatedAt;

    private Float average_rating;

    public static RowMapper<PlaylistEntity> getRowMapper() {
        return (rs, rowNum) -> PlaylistEntity.builder()
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
                .average_rating(rs.getFloat("average_rating"))
                .build();
    }

    public Playlist toPlaylist() {
        return Playlist.builder()
                .code(playlistCode)
                .title(title)
                .channel(channel)
                .thumbnail(thumbnail)
                .description(description)
                .publishedAt(publishedAt)
                .videoCount(videoCount)
                .build();
    }

    public PlaylistEntity(Playlist playlist, int playlistDuration) {
        this.playlistCode = playlist.getCode();
        this.title = playlist.getTitle();
        this.channel = playlist.getChannel();
        this.thumbnail = playlist.getThumbnail();
        this.description = playlist.getDescription();
        this.publishedAt = playlist.getPublishedAt();
        this.videoCount = playlist.getVideoCount();
        this.duration = playlistDuration;
    }

    public PlaylistEntity updateByYoutube(Playlist playlist, int playlistDuration) {
        this.channel = playlist.getChannel();
        this.thumbnail = playlist.getThumbnail();
        this.description = playlist.getDescription();
        this.duration = playlistDuration;
        this.title = playlist.getTitle();
        this.videoCount = playlist.getVideoCount();
        return this;
    }
}
