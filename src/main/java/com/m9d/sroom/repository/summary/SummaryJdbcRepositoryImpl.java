package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.mapper.Summary;
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
    public Summary save(Summary summary) {
        jdbcTemplate.update(SummaryRepositorySql.SAVE,
                summary.getVideoId(),
                summary.getContent(),
                summary.isModified());
        return getById(jdbcTemplate.queryForObject(SummaryRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public Summary getById(Long summaryId) {
        return jdbcTemplate.queryForObject(SummaryRepositorySql.GET_BY_ID, Summary.getRowMapper(), summaryId);
    }

    @Override
    public Optional<Summary> findById(Long summaryId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SummaryRepositorySql.GET_BY_ID, Summary.getRowMapper(), summaryId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Summary updateById(Long summaryId, Summary summary) {
        jdbcTemplate.update(SummaryRepositorySql.UPDATE_BY_ID,
                summary.getContent(),
                summary.isModified(),
                summaryId);
        return getById(summaryId);
    }
}
