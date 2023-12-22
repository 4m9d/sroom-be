package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.jpa.MaterialFeedbackEntity;
import com.m9d.sroom.material.dto.response.FeedbackInfo;
import com.m9d.sroom.material.model.MaterialType;

public class MaterialMapper {

    public static FeedbackInfo getInfoByEntity(MaterialFeedbackEntity feedbackEntity) {
        return FeedbackInfo.builder()
                .type(MaterialType.from(feedbackEntity.getContentType()).toStr())
                .available(false)
                .hasFeedback(true)
                .isSatisfactory(feedbackEntity.getRating())
                .build();
    }

}
