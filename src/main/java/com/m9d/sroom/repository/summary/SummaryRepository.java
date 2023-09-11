package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.model.Summary;
import com.m9d.sroom.material.dto.response.SummaryBrief;

import java.util.Optional;

public interface SummaryRepository {

    void save(Summary summary);

    Optional<Summary> findByCourseVideoId(Long courseVideoId);

    SummaryBrief getSummaryBriefById(Long summaryId);

    void update(Long summaryId, String content);
}
