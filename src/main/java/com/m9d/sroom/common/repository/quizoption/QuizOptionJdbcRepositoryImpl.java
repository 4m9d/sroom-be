package com.m9d.sroom.common.repository.quizoption;

import com.m9d.sroom.common.dto.QuizOptionDto;
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
    public QuizOptionDto save(QuizOptionDto quizOptionDto) {
        jdbcTemplate.update(QuizOptionRepositorySql.SAVE,
                quizOptionDto.getQuizId(),
                quizOptionDto.getOptionText(),
                quizOptionDto.getOptionIndex());
        return getById(jdbcTemplate.queryForObject(QuizOptionRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public QuizOptionDto getById(Long quizOptionId) {
        return jdbcTemplate.queryForObject(QuizOptionRepositorySql.GET_BY_ID, QuizOptionDto.getRowMapper(), quizOptionId);
    }

    @Override
    public List<QuizOptionDto> getListByQuizId(Long quizId) {
        return jdbcTemplate.query(QuizOptionRepositorySql.GET_LIST_BY_QUIZ_ID, QuizOptionDto.getRowMapper(), quizId);
    }
}
