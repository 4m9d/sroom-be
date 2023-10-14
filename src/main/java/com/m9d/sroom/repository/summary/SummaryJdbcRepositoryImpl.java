package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.mapper.SummaryDto;
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
    public SummaryDto save(SummaryDto summaryDto) {
        jdbcTemplate.update(SummaryRepositorySql.SAVE,
                summaryDto.getVideoId(),
                summaryDto.getContent(),
                summaryDto.isModified());
        return getById(jdbcTemplate.queryForObject(SummaryRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public SummaryDto getById(Long summaryId) {
        return jdbcTemplate.queryForObject(SummaryRepositorySql.GET_BY_ID, SummaryDto.getRowMapper(), summaryId);
    }

    @Override
    public Optional<SummaryDto> findById(Long summaryId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SummaryRepositorySql.GET_BY_ID, SummaryDto.getRowMapper(), summaryId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public SummaryDto updateById(Long summaryId, SummaryDto summaryDto) {
        jdbcTemplate.update(SummaryRepositorySql.UPDATE_BY_ID,
                summaryDto.getContent(),
                summaryDto.isModified(),
                summaryId);
        return getById(summaryId);
    }
}
