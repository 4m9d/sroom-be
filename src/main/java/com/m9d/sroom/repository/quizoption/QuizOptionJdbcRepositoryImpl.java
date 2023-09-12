package com.m9d.sroom.repository.quizoption;

import com.m9d.sroom.global.model.QuizOption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuizOptionJdbcRepositoryImpl implements QuizOptionRepository{
    @Override
    public void save(QuizOption quizOption) {

    }

    @Override
    public List<QuizOption> getQuizOptionListByQuizId(Long quizId) {
        return null;
    }
}
