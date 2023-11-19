package com.m9d.sroom.common.repository.summary;

import com.m9d.sroom.common.entity.SummaryEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SummaryJdbcRepositoryImpl implements SummaryRepository {

    private final JdbcTemplate jdbcTemplate;

    public SummaryJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SummaryEntity save(SummaryEntity summary) {
        jdbcTemplate.update(SummaryRepositorySql.SAVE,
                summary.getVideoId(),
                summary.getContent(),
                summary.isModified());
        return getById(jdbcTemplate.queryForObject(SummaryRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public SummaryEntity getById(Long summaryId) {
        return jdbcTemplate.queryForObject(SummaryRepositorySql.GET_BY_ID, SummaryEntity.getRowMapper(), summaryId);
    }

    @Override
    public Optional<SummaryEntity> findById(Long summaryId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SummaryRepositorySql.GET_BY_ID, SummaryEntity.getRowMapper(), summaryId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public SummaryEntity updateById(Long summaryId, SummaryEntity summary) {
        jdbcTemplate.update(SummaryRepositorySql.UPDATE_BY_ID,
                summary.getContent(),
                summary.isModified(),
                summaryId);
        return getById(summaryId);
    }

    @Override
    public void feedbackPositive(Long summaryId) {
        jdbcTemplate.update(SummaryRepositorySql.UPDATE_POSITIVE_FEEDBACK_COUNT, summaryId);
    }

    @Override
    public void feedbackNegative(Long summaryId) {
        jdbcTemplate.update(SummaryRepositorySql.UPDATE_NEGATIVE_FEEDBACK_COUNT, summaryId);
    }
}
