package com.m9d.sroom.repository.quiz;


import com.m9d.sroom.global.model.Quiz;

import java.util.List;

public interface QuizRepository {

    void save(Quiz quiz);

    List<Quiz> getListByVideoId(Long videoId);
}
