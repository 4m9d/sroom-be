package com.m9d.sroom.common.repository.quiz;

import com.m9d.sroom.common.dto.Quiz;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizJdbcRepositoryImpl implements QuizRepository {

    private final JdbcTemplate jdbcTemplate;

    public QuizJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Quiz save(Quiz quiz) {
        jdbcTemplate.update(QuizRepositorySql.SAVE,
                quiz.getVideoId(),
                quiz.getType(),
                quiz.getQuestion(),
                quiz.getSubjectiveAnswer(),
                quiz.getChoiceAnswer());
        return getById(jdbcTemplate.queryForObject(QuizRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public List<Quiz> getListByVideoId(Long videoId) {
        return jdbcTemplate.query(QuizRepositorySql.GET_LIST_BY_VIDEO_ID, Quiz.getRowMapper(), videoId);
    }

    @Override
    public Quiz getById(Long quizId) {
        return jdbcTemplate.queryForObject(QuizRepositorySql.GET_BY_ID, Quiz.getRowMapper(), quizId);
    }

    @Override
    public Optional<Quiz> findById(Long quizId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(QuizRepositorySql.GET_BY_ID, Quiz.getRowMapper(), quizId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
