package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.mapper.SummaryDto;

import java.util.Optional;

public interface SummaryRepository {

    SummaryDto save(SummaryDto summaryDto);

    SummaryDto getById(Long summaryId);

    Optional<SummaryDto> findById(Long summaryId);

    SummaryDto updateById(Long summaryId, SummaryDto summaryDto);
}
