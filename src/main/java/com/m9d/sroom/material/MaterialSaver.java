package com.m9d.sroom.material;

import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import com.m9d.sroom.ai.model.MaterialVaildStatus;
import com.m9d.sroom.ai.vo.MaterialResultsVo;
import com.m9d.sroom.common.entity.QuizEntity;
import com.m9d.sroom.common.entity.QuizOptionEntity;
import com.m9d.sroom.common.entity.SummaryEntity;
import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.common.repository.summary.SummaryRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.material.exception.VideoNotFoundFromDBException;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.quiz.Quiz;
import com.m9d.sroom.quiz.QuizOption;
import com.m9d.sroom.summary.Summary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MaterialSaver {

    private final VideoRepository videoRepository;
    private final SummaryRepository summaryRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final QuizRepository quizRepository;
    private final QuizOptionRepository quizOptionRepository;

    public MaterialSaver(VideoRepository videoRepository, SummaryRepository summaryRepository, CourseVideoRepository courseVideoRepository, QuizRepository quizRepository, QuizOptionRepository quizOptionRepository) {
        this.videoRepository = videoRepository;
        this.summaryRepository = summaryRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.quizRepository = quizRepository;
        this.quizOptionRepository = quizOptionRepository;
    }

    @Transactional
    public void saveMaterials(MaterialResultsVo materialVo) throws VideoNotFoundFromDBException {
        VideoEntity videoEntity = videoRepository.findByCode(materialVo.getVideoId())
                .orElseThrow(() -> {
                    log.warn("can't find video information from db. video code = {}", materialVo.getVideoId());
                    return new VideoNotFoundFromDBException();
                });

        Summary summary = materialVo.getSummary();

        long summaryId;
        if (materialVo.getIsValid() == MaterialVaildStatus.IN_VALID.getValue()) {
            videoEntity.setMaterialStatus(MaterialStatus.CREATION_FAILED.getValue());
            summaryId = MaterialStatus.CREATION_FAILED.getValue();
        } else {
            videoEntity.setMaterialStatus(MaterialStatus.CREATED.getValue());
            summaryId = summaryRepository.save(new SummaryEntity(videoEntity.getVideoId(), summary.getContent(),
                            false))
                    .getId();

            for (Quiz quiz : materialVo.getQuizList()) {
                saveQuiz(videoEntity.getVideoId(), quiz);
            }
        }

        videoEntity.setSummaryId(summaryId);
        videoRepository.updateById(videoEntity.getVideoId(), videoEntity);
        courseVideoRepository.updateSummaryId(videoEntity.getVideoId(), summaryId);
    }

    private void saveQuiz(Long videoId, Quiz quiz) {
        switch (quiz.getType()) {
            case TRUE_FALSE:
                quizRepository.save(QuizEntity.createMultipleChoiceQuizEntity(videoId, quiz));
                break;
            case MULTIPLE_CHOICE:
                QuizEntity quizEntity = quizRepository.save(QuizEntity.createMultipleChoiceQuizEntity(videoId, quiz));
                saveQuizOptions(quizEntity.getId(), quiz.getQuizOptionList());
                break;
            case SUBJECTIVE:
                quizRepository.save(QuizEntity.createShortAnswerQuizEntity(videoId, quiz));
                break;
            default:
                throw new QuizTypeNotMatchException(quiz.getType().getValue());
        }
    }

    private void saveQuizOptions(Long quizId, List<QuizOption> quizOptionList) {
        for (QuizOption option : quizOptionList) {
            quizOptionRepository.save(new QuizOptionEntity(quizId, option));
        }
    }
}
