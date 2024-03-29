package com.m9d.sroom.common.entity.jdbctemplate;


import com.m9d.sroom.summary.Summary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
public class SummaryEntity {

    private Long id;

    private Long videoId;

    private String content;

    private Timestamp updatedAt;

    private boolean modified;

    private Integer positiveFeedbackCount;

    private Integer negativeFeedbackCount;

    public static RowMapper<SummaryEntity> getRowMapper() {
        return (rs, rowNum) -> SummaryEntity.builder()
                .id(rs.getLong("summary_id"))
                .videoId(rs.getLong("video_id"))
                .content(rs.getString("content"))
                .updatedAt(rs.getTimestamp("updated_time"))
                .modified(rs.getBoolean("is_modified"))
                .positiveFeedbackCount(rs.getInt("positive_feedback_count"))
                .negativeFeedbackCount(rs.getInt("negative_feedback_count"))
                .build();
    }

    public Summary toSummary(){
        return new Summary(content, updatedAt ,modified);
    }

    public SummaryEntity(Long videoId, String content, boolean modified) {
        this.videoId = videoId;
        this.content = content;
        this.modified = modified;
    }
}
