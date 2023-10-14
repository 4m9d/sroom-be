package com.m9d.sroom.common.repository.quiz;

import com.m9d.sroom.common.dto.QuizDto;
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
    public QuizDto save(QuizDto quizDto) {
        jdbcTemplate.update(QuizRepositorySql.SAVE,
                quizDto.getVideoId(),
                quizDto.getType(),
                quizDto.getQuestion(),
                quizDto.getSubjectiveAnswer(),
                quizDto.getChoiceAnswer());
        return getById(jdbcTemplate.queryForObject(QuizRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public List<QuizDto> getListByVideoId(Long videoId) {
        return jdbcTemplate.query(QuizRepositorySql.GET_LIST_BY_VIDEO_ID, QuizDto.getRowMapper(), videoId);
    }

    @Override
    public QuizDto getById(Long quizId) {
        return jdbcTemplate.queryForObject(QuizRepositorySql.GET_BY_ID, QuizDto.getRowMapper(), quizId);
    }

    @Override
    public Optional<QuizDto> findById(Long quizId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(QuizRepositorySql.GET_BY_ID, QuizDto.getRowMapper(), quizId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
