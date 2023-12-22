package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.jpa.MaterialFeedbackEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.QuizEntity;
import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import com.m9d.sroom.common.repository.materialfeedback.MaterialFeedbackJpaRepository;
import com.m9d.sroom.common.repository.quiz.QuizJpaRepository;
import com.m9d.sroom.common.repository.summary.SummaryJpaRepository;
import com.m9d.sroom.material.dto.response.FeedbackInfo;
import com.m9d.sroom.material.exception.MaterialFeedbackDuplicateException;
import com.m9d.sroom.material.exception.QuizNotFoundException;
import com.m9d.sroom.material.exception.SummaryNotFoundException;
import com.m9d.sroom.material.model.MaterialType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FeedbackServiceVJpa {

    private final MaterialFeedbackJpaRepository feedbackRepository;
    private final SummaryJpaRepository summaryRepository;
    private final QuizJpaRepository quizRepository;

    public FeedbackServiceVJpa(MaterialFeedbackJpaRepository feedbackRepository, SummaryJpaRepository summaryRepository, QuizJpaRepository quizRepository) {
        this.feedbackRepository = feedbackRepository;
        this.summaryRepository = summaryRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional
    public FeedbackInfo feedback(MemberEntity memberEntity, MaterialType materialType, Long materialId,
                                 boolean satisfactory) {
        Optional<MaterialFeedbackEntity> feedbackEntityOptional =
                memberEntity.findFeedbackByMaterialIdAndType(materialType, materialId);

        if (feedbackEntityOptional.isPresent()) {
            throw new MaterialFeedbackDuplicateException();
        } else {
            MaterialFeedbackEntity newFeedbackEntity = feedbackRepository.save(MaterialFeedbackEntity.create(memberEntity,
                    materialId, materialType.getValue(), satisfactory));

            updateFeedbackCount(materialType, materialId, satisfactory);
            return MaterialMapper.getInfoByEntity(newFeedbackEntity);
        }
    }

    private void updateFeedbackCount(MaterialType materialType, Long materialId, boolean satisfactory) {
        if (materialType.equals(MaterialType.SUMMARY)) {
            SummaryEntity summaryEntity = summaryRepository.findById(materialId)
                    .orElseThrow(SummaryNotFoundException::new);

            summaryEntity.feedback(satisfactory);
        } else {
            QuizEntity quizEntity = quizRepository.findById(materialId)
                    .orElseThrow(QuizNotFoundException::new);

            quizEntity.feedback(satisfactory);
        }
    }
}
