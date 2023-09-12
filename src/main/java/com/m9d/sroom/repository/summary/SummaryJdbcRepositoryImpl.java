package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.model.Summary;
import com.m9d.sroom.material.dto.response.SummaryBrief;
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
    public SummaryBrief getSummaryBriefById(Long summaryId) {
        return null;
    }

    @Override
    public void update(Long summaryId, String content) {

    }
}
