package com.m9d.sroom.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;


@Data
@Builder
@AllArgsConstructor
public class RecommendEntity {

    private String sourceCode;

    private Boolean isPlaylist;

    private Integer domain;

    public static RowMapper<RecommendEntity> getMapper() {
        return (rs, rowNum) -> RecommendEntity.builder()
                .sourceCode(rs.getString("source_code"))
                .isPlaylist(rs.getBoolean("is_playlist"))
                .domain(rs.getInt("domain"))
                .build();
    }
}
