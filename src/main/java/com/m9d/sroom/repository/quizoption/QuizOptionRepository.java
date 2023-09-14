package com.m9d.sroom.repository.quizoption;

import com.m9d.sroom.global.mapper.QuizOption;

import java.util.List;

public interface QuizOptionRepository {

    void save(QuizOption quizOption);

    List<QuizOption> getListByQuizId(Long quizId);
}
