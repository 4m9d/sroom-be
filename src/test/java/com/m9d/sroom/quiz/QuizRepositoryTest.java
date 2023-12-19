package com.m9d.sroom.quiz;

import com.m9d.sroom.common.entity.jpa.QuizEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.common.repository.quiz.QuizJpaRepository;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QuizRepositoryTest extends RepositoryTest {

    @Autowired
    private QuizJpaRepository quizRepository;

    @Test
    @DisplayName("퀴즈를 저장합니다.")
    void saveQuiz() {
        //given
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);

        //when
        quizRepository.save(QuizEntity.creatChoiceType(video, "QUIZ", 1));

        //then
        Assertions.assertEquals(quizRepository.getById(1L).getQuestion(), "QUIZ");
    }
}
