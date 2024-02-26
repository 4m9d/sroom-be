package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.jdbctemplate.MaterialFeedbackEntity;
import com.m9d.sroom.common.repository.materialfeedback.MaterialFeedbackRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.summary.SummaryRepository;
import com.m9d.sroom.material.dto.response.FeedbackInfo;
import com.m9d.sroom.material.exception.FeedbackUnavailableException;
import com.m9d.sroom.material.exception.MaterialFeedbackDuplicateException;
import com.m9d.sroom.material.exception.QuizNotFoundException;
import com.m9d.sroom.material.exception.SummaryNotFoundException;
import com.m9d.sroom.material.model.MaterialType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FeedbackService {

    private final MaterialFeedbackRepository feedbackRepository;
    private final SummaryRepository summaryRepository;
    private final QuizRepository quizRepository;

    public FeedbackService(MaterialFeedbackRepository feedbackRepository, SummaryRepository summaryRepository,
                           QuizRepository quizRepository) {
        this.feedbackRepository = feedbackRepository;
        this.summaryRepository = summaryRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional
    public FeedbackInfo feedback(Long memberId, MaterialType materialType, Long materialId, boolean available,
                                 boolean satisfactory) {
        FeedbackInfo feedbackInfo = getFeedbackInfo(memberId, materialType, materialId);

        if (feedbackInfo.isHasFeedback()) {
            throw new MaterialFeedbackDuplicateException();
        } else if (!available) {
            throw new FeedbackUnavailableException();
        } else {
            MaterialFeedbackEntity feedbackEntity = feedbackRepository.save(
                    MaterialFeedbackEntity.createForSave(memberId, materialType, materialId, satisfactory));
            updateFeedbackCount(materialType, materialId, satisfactory);
            return FeedbackInfo.createSubmittedInfo(feedbackEntity);
        }

    }

    public FeedbackInfo getFeedbackInfo(Long memberId, MaterialType materialType, Long materialId) {
        Optional<MaterialFeedbackEntity> feedbackEntityOptional =
                feedbackRepository.findByMemberIdAndTypeAndMaterialId(memberId, materialType.getValue(), materialId);

        return feedbackEntityOptional.map(FeedbackInfo::createSubmittedInfo)
                .orElseGet(() -> FeedbackInfo.getNoSubmittedInfo(materialType));
    }

    private void updateFeedbackCount(MaterialType materialType, Long materialId, boolean satisfactory) {
        if (materialType.equals(MaterialType.SUMMARY)) {
            summaryRepository.findById(materialId).orElseThrow(SummaryNotFoundException::new);

            if (satisfactory) {
                summaryRepository.feedbackPositive(materialId);
            } else {
                summaryRepository.feedbackNegative(materialId);
            }
        } else {
            quizRepository.findById(materialId).orElseThrow(QuizNotFoundException::new);

            if (satisfactory) {
                quizRepository.feedbackPositive(materialId);
            } else {
                quizRepository.feedbackNegative(materialId);
            }
        }
    }
}
