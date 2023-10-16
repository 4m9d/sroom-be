package com.m9d.sroom.playlist;

import com.m9d.sroom.video.VideoDto;
import com.m9d.sroom.youtube.dto.PlaylistInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
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

    public PlaylistDto(Playlist playlist, int duration) {
        this.playlistCode = playlist.getCode();
        this.title = playlist.getTitle();
        this.channel = playlist.getChannel();
        this.thumbnail = playlist.getThumbnail();
        this.description = playlist.getDescription();
        this.publishedAt = playlist.getPublishedAt();
        this.videoCount = playlist.getVideoCount();
        this.duration = duration;
    }

    public PlaylistInfo toPlaylistInfo() {
        return PlaylistInfo.builder()
                .code(playlistCode)
                .title(title)
                .channel(channel)
                .thumbnail(thumbnail)
                .description(description)
                .publishedAt(publishedAt)
                .videoCount(videoCount)
                .build();
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
