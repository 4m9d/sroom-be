package com.m9d.sroom.common.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class LectureEntity {

    private Long id;

    private Long memberId;

    private Long courseId;

    private Long sourceId;

    private Boolean playlist;

    private Integer lectureIndex;

    private Boolean reviewed;

    private String channel;

    public static RowMapper<LectureEntity> getRowMapper() {
        return (rs, rowNum) -> LectureEntity.builder()
                .id(rs.getLong("lecture_id"))
                .courseId(rs.getLong("course_id"))
                .sourceId(rs.getLong("source_id"))
                .playlist(rs.getBoolean("is_playlist"))
                .lectureIndex(rs.getInt("lecture_index"))
                .reviewed(rs.getBoolean("is_reviewed"))
                .memberId(rs.getLong("member_id"))
                .channel(rs.getString("channel"))
                .build();
    }
}
