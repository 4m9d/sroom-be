package com.m9d.sroom.common.repository.quiz;


import com.m9d.sroom.common.entity.jdbctemplate.QuizEntity;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {

    QuizEntity save(QuizEntity quiz);

    List<QuizEntity> getListByVideoId(Long videoId);

    QuizEntity getById(Long quizId);

    Optional<QuizEntity> findById(Long quizId);

    void feedbackPositive(Long quizId);

    void feedbackNegative(Long quizId);
}
