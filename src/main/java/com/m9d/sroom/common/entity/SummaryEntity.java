package com.m9d.sroom.common.entity;


import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Data
@Builder
public class SummaryEntity {

    private Long id;

    private Long courseId;

    private Long videoId;

    private Long courseVideoId;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean modified;

    public static RowMapper<SummaryEntity> getRowMapper() {
        return (rs, rowNum) -> SummaryEntity.builder()
                .id(rs.getLong("summary_id"))
                .videoId(rs.getLong("video_id"))
                .content(rs.getString("content"))
                .updatedAt(rs.getTimestamp("updated_time"))
                .modified(rs.getBoolean("is_modified"))
                .build();
    }
}
