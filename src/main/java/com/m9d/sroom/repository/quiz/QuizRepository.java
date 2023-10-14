package com.m9d.sroom.repository.quiz;


import com.m9d.sroom.global.mapper.QuizDto;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {

    QuizDto save(QuizDto quizDto);

    List<QuizDto> getListByVideoId(Long videoId);

    QuizDto getById(Long quizId);

    Optional<QuizDto> findById(Long quizId);
}
