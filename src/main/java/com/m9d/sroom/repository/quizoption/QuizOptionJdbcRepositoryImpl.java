package com.m9d.sroom.repository.quizoption;

import com.m9d.sroom.global.mapper.QuizOption;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuizOptionJdbcRepositoryImpl implements QuizOptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public QuizOptionJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public QuizOption save(QuizOption quizOption) {
        jdbcTemplate.update(QuizOptionRepositorySql.SAVE,
                quizOption.getQuizId(),
                quizOption.getOptionText(),
                quizOption.getOptionIndex());
        return getById(jdbcTemplate.queryForObject(QuizOptionRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public QuizOption getById(Long quizOptionId) {
        return jdbcTemplate.queryForObject(QuizOptionRepositorySql.GET_BY_ID, QuizOption.getRowMapper(), quizOptionId);
    }

    @Override
    public List<QuizOption> getListByQuizId(Long quizId) {
        return jdbcTemplate.query(QuizOptionRepositorySql.GET_LIST_BY_QUIZ_ID, QuizOption.getRowMapper(), quizId);
    }
}
