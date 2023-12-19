package com.m9d.sroom.quizOption;

import com.m9d.sroom.common.entity.jpa.QuizEntity;
import com.m9d.sroom.common.entity.jpa.QuizOptionEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.common.repository.quizoption.QuizOptionJpaRepository;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class quizOptionRepositoryTest extends RepositoryTest {

    @Autowired
    private QuizOptionJpaRepository quizOptionRepository;

    @Test
    @DisplayName("퀴즈 선택 옵션을 저장합니다.")
    void saveQuizOption() {
        //given
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);
        QuizEntity quiz = QuizEntity.creatChoiceType(video, "QUIZ", 1);
        quizRepository.save(quiz);

        //when
        quizOptionRepository.save(QuizOptionEntity.create(quiz, "Option1", 1));
        quizOptionRepository.save(QuizOptionEntity.create(quiz, "Option2", 2));

        //then
        Assertions.assertEquals(quizOptionRepository.getById(1L).getOptionText(), "Option1");
        Assertions.assertEquals(quizOptionRepository.getById(2L).getOptionText(), "Option2");
    }
}
