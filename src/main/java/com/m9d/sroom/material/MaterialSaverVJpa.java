package com.m9d.sroom.material;

import com.m9d.sroom.ai.model.MaterialVaildStatus;
import com.m9d.sroom.ai.vo.MaterialResultVo;
import com.m9d.sroom.ai.vo.QuizVo;
import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoJpaRepository;
import com.m9d.sroom.common.repository.quiz.QuizJpaRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionJpaRepository;
import com.m9d.sroom.common.repository.summary.SummaryJpaRepository;
import com.m9d.sroom.common.repository.video.VideoJpaRepository;
import com.m9d.sroom.material.exception.VideoNotFoundFromDBException;
import com.m9d.sroom.material.model.MaterialStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MaterialSaverVJpa {

    private final VideoJpaRepository videoRepository;
    private final SummaryJpaRepository summaryRepository;
    private final QuizJpaRepository quizRepository;
    private final CourseVideoJpaRepository courseVideoRepository;
    private final QuizOptionJpaRepository quizOptionRepository;

    public MaterialSaverVJpa(VideoJpaRepository videoRepository, SummaryJpaRepository summaryRepository, QuizJpaRepository quizRepository, CourseVideoJpaRepository courseVideoRepository, QuizOptionJpaRepository quizOptionRepository) {
        this.videoRepository = videoRepository;
        this.summaryRepository = summaryRepository;
        this.quizRepository = quizRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.quizOptionRepository = quizOptionRepository;
    }

    @Transactional
    public void saveMaterials(MaterialResultVo materialVo) throws VideoNotFoundFromDBException {
        VideoEntity videoEntity = videoRepository.findByCode(materialVo.getVideoId())
                .orElseThrow(() -> {
                    log.warn("can't find video information from db. video code = {}", materialVo.getVideoId());
                    return new VideoNotFoundFromDBException();
                });

        if (materialVo.getIsValid() == MaterialVaildStatus.IN_VALID.getValue()) {
            videoEntity.setMaterialStatus(MaterialStatus.CREATION_FAILED);
            videoEntity.setSummary(null);
        } else {
            videoEntity.setMaterialStatus(MaterialStatus.CREATED);
            videoEntity.setSummary(summaryRepository.save(SummaryEntity.create(videoEntity, materialVo.getSummary())));

            for (QuizVo quizVo : materialVo.getQuizzes()) {
                saveQuiz(quizVo, videoEntity);
            }
        }

        for (CourseVideoEntity courseVideoEntity : courseVideoRepository.findListByVideoId(videoEntity.getVideoId())) {
            courseVideoEntity.setSummary(videoEntity.getSummary());
        }
        log.info("subject = materialSaved, quizCount = {}", materialVo.getQuizList().size());
    }

    private void saveQuiz(QuizVo quizVo, VideoEntity videoEntity) {
        QuizEntity quizEntity = QuizEntity.createChoiceType(videoEntity, quizVo.getQuizQuestion(),
                Integer.parseInt(quizVo.getAnswer()));
        quizRepository.save(quizEntity);

        saveQuizOptions(quizVo.getOptions(), quizEntity);
    }

    private void saveQuizOptions(List<String> quizOptionsList, QuizEntity quizEntity) {
        for (int i = 0; i < quizOptionsList.size(); i++) {
            quizOptionRepository.save(QuizOptionEntity.create(quizEntity, quizOptionsList.get(i), i + 1));
        }
    }
}
