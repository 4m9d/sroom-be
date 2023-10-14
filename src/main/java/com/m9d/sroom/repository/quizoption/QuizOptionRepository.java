package com.m9d.sroom.repository.quizoption;

import com.m9d.sroom.global.mapper.QuizOptionDto;

import java.util.List;

public interface QuizOptionRepository {

    QuizOptionDto save(QuizOptionDto quizOptionDto);

    QuizOptionDto getById(Long quizOptionId);

    List<QuizOptionDto> getListByQuizId(Long quizId);
}
