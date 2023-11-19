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

    private boolean satisfactory;

    public static RowMapper<MaterialFeedbackEntity> getRowMapper() {
        return (rs, rowNum) -> MaterialFeedbackEntity.builder()
                .feedbackId(rs.getLong("feedback_id"))
                .memberId(rs.getLong("member_id"))
                .contentId(rs.getLong("content_id"))
                .contentType(rs.getInt("content_type"))
                .satisfactory(rs.getBoolean("rating"))
                .build();
    }

    public MaterialType getContentType() {
        return MaterialType.from(contentType);
    }

    public static MaterialFeedbackEntity createForSave(Long memberId, MaterialType type, Long contentId,
                                                       boolean satisfactory) {
        return MaterialFeedbackEntity.builder()
                .memberId(memberId)
                .contentType(type.getValue())
                .contentId(contentId)
                .satisfactory(satisfactory)
                .build();
    }
}
