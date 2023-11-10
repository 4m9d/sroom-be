package com.m9d.sroom.common.entity;

import com.m9d.sroom.material.model.MaterialType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class MaterialFeedbackEntity {

    @Id
    private Long feedbackId;

    private Long memberId;

    private Long contentId;

    private int contentType;

    private int rating;

    public static RowMapper<MaterialFeedbackEntity> getRowMapper() {
        return (rs, rowNum) -> MaterialFeedbackEntity.builder()
                .feedbackId(rs.getLong("feedback_id"))
                .memberId(rs.getLong("member_id"))
                .contentId(rs.getLong("content_id"))
                .contentType(rs.getInt("content_type"))
                .rating(rs.getInt("rating"))
                .build();
    }

    public MaterialType getContentType() {
        return MaterialType.from(contentType);
    }
}
