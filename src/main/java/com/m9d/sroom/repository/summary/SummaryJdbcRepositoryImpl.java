package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.mapper.Summary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SummaryJdbcRepositoryImpl implements SummaryRepository{
    @Override
    public void save(Summary summary) {

    }

    @Override
    public Optional<Summary> findByCourseVideoId(Long courseVideoId) {
        return Optional.empty();
    }

    @Override
    public void update(Summary summary) {

    }
}
