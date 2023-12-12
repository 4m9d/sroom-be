package com.m9d.sroom.common.repository.quizoption;

import com.m9d.sroom.common.entity.jdbctemplate.QuizOptionEntity;
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
    public QuizOptionEntity save(QuizOptionEntity quizOption) {
        jdbcTemplate.update(QuizOptionRepositorySql.SAVE,
                quizOption.getQuizId(),
                quizOption.getOptionText(),
                quizOption.getOptionIndex());
        return getById(jdbcTemplate.queryForObject(QuizOptionRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public QuizOptionEntity getById(Long quizOptionId) {
        return jdbcTemplate.queryForObject(QuizOptionRepositorySql.GET_BY_ID, QuizOptionEntity.getRowMapper(), quizOptionId);
    }

    @Override
    public List<QuizOptionEntity> getListByQuizId(Long quizId) {
        return jdbcTemplate.query(QuizOptionRepositorySql.GET_LIST_BY_QUIZ_ID, QuizOptionEntity.getRowMapper(), quizId);
    }
}
