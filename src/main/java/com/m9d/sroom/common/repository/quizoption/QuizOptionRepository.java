package com.m9d.sroom.common.repository.quizoption;

import com.m9d.sroom.common.entity.QuizOptionEntity;

import java.util.List;

public interface QuizOptionRepository {

    QuizOptionEntity save(QuizOptionEntity quizOption);

    QuizOptionEntity getById(Long quizOptionId);

    List<QuizOptionEntity> getListByQuizId(Long quizId);
}
