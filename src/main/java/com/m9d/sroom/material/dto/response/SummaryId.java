package com.m9d.sroom.material.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryId {

    private Long summaryId;

    public SummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }
}
