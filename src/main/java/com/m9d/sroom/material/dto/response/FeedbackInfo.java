package com.m9d.sroom.material.dto.response;

import com.m9d.sroom.common.entity.jdbctemplate.MaterialFeedbackEntity;
import com.m9d.sroom.material.model.MaterialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FeedbackInfo {

    private String type;

    private boolean available;

    private boolean hasFeedback;

    private boolean isSatisfactory;

    public static FeedbackInfo getNoSubmittedInfo(MaterialType materialType) {
        return new FeedbackInfo(materialType.toStr(), true, false, false);
    }

    public static FeedbackInfo createSubmittedInfo(MaterialFeedbackEntity materialFeedbackEntity) {
        return new FeedbackInfo(materialFeedbackEntity.getContentType().toStr(), false, true,
                materialFeedbackEntity.isSatisfactory());
    }
}
