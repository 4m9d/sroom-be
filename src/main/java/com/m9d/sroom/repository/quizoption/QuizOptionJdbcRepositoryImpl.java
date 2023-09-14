package com.m9d.sroom.repository.quizoption;

import com.m9d.sroom.global.mapper.QuizOption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuizOptionJdbcRepositoryImpl implements QuizOptionRepository{
    @Override
    public void save(QuizOption quizOption) {

    }

    @Override
    public List<QuizOption> getListByQuizId(Long quizId) {
        return null;
    }
}
