package com.m9d.sroom.common.repository.quizoption;

import com.m9d.sroom.common.dto.QuizOptionDto;

import java.util.List;

public interface QuizOptionRepository {

    QuizOptionDto save(QuizOptionDto quizOptionDto);

    QuizOptionDto getById(Long quizOptionId);

    List<QuizOptionDto> getListByQuizId(Long quizId);
}
