package com.m9d.sroom.common.entity.jdbctemplate;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class PlaylistVideoEntity {

    private Long id;

    private Long playlistId;

    private Long videoId;

    private Integer videoIndex;

    public static RowMapper<PlaylistVideoEntity> getMapper() {
        return (rs, rowNum) -> PlaylistVideoEntity.builder()
                .id(rs.getLong("playlist_video_id"))
                .playlistId(rs.getLong("playlist_id"))
                .videoId(rs.getLong("video_id"))
                .videoIndex(rs.getInt("video_index"))
                .build();
    }
}
