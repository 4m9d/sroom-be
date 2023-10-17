package com.m9d.sroom.common.repository.summary;

import com.m9d.sroom.common.entity.SummaryEntity;

import java.util.Optional;

public interface SummaryRepository {

    SummaryEntity save(SummaryEntity summary);

    SummaryEntity getById(Long summaryId);

    Optional<SummaryEntity> findById(Long summaryId);

    SummaryEntity updateById(Long summaryId, SummaryEntity summary);
}
