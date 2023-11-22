package com.m9d.sroom.common.repository.materialfeedback;

import com.m9d.sroom.common.entity.MaterialFeedbackEntity;

import java.util.Optional;

public interface MaterialFeedbackRepository {

    MaterialFeedbackEntity save(MaterialFeedbackEntity feedbackEntity);

    MaterialFeedbackEntity getById(Long id);

    Optional<MaterialFeedbackEntity> findByMemberIdAndTypeAndMaterialId(Long memberId, int materialType,
                                                                        Long materialId);
}
