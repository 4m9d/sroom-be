package com.m9d.sroom.common.repository.quizoption;

import com.m9d.sroom.common.dto.QuizOption;

import java.util.List;

public interface QuizOptionRepository {

    QuizOption save(QuizOption quizOption);

    QuizOption getById(Long quizOptionId);

    List<QuizOption> getListByQuizId(Long quizId);
}
