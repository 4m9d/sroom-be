package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class PlaylistVideo {

    private Long id;

    private Long playlistId;

    private Long videoId;

    private Integer videoIndex;

    public static RowMapper<PlaylistVideo> getMapper() {
        return (rs, rowNum) -> PlaylistVideo.builder()
                .id(rs.getLong("playlist_video_id"))
                .playlistId(rs.getLong("playlist_id"))
                .videoId(rs.getLong("video_id"))
                .videoIndex(rs.getInt("video_index"))
                .build();
    }
}
