package com.m9d.sroom.common.repository.materialfeedback;

import com.m9d.sroom.common.entity.MaterialFeedbackEntity;

public interface MaterialFeedbackRepository {

    MaterialFeedbackEntity save(MaterialFeedbackEntity feedbackEntity);

    MaterialFeedbackEntity getById(Long id);

    Boolean checkQuizFeedbackExist(Long memberId, Long quizId);

    Boolean checkSummaryFeedbackExist(Long memberId, Long summaryId);
}
