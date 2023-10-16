package com.m9d.sroom.common.repository.quiz;


import com.m9d.sroom.common.dto.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {

    Quiz save(Quiz quiz);

    List<Quiz> getListByVideoId(Long videoId);

    Quiz getById(Long quizId);

    Optional<Quiz> findById(Long quizId);
}
