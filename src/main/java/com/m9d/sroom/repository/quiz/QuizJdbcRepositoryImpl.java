package com.m9d.sroom.repository.quiz;

import com.m9d.sroom.global.mapper.Quiz;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuizJdbcRepositoryImpl implements QuizRepository{
    @Override
    public void save(Quiz quiz) {

    }

    @Override
    public List<Quiz> getListByVideoId(Long videoId) {
        return null;
    }
}
