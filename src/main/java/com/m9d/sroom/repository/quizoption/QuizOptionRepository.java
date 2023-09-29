package com.m9d.sroom.repository.quizoption;

import com.m9d.sroom.global.mapper.QuizOption;

import java.util.List;

public interface QuizOptionRepository {

    QuizOption save(QuizOption quizOption);

    QuizOption getById(Long quizOptionId);

    List<QuizOption> getListByQuizId(Long quizId);
}
