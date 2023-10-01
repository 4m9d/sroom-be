package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.mapper.Summary;

import java.util.Optional;

public interface SummaryRepository {

    Summary save(Summary summary);

    Summary getById(Long summaryId);

    Optional<Summary> findById(Long summaryId);

    Summary updateById(Long summaryId, Summary summary);
}
