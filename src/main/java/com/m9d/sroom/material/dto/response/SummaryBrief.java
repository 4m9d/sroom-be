package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.global.mapper.SummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryBrief {

    private String content;

    @JsonProperty("is_modified")
    private boolean modified;

    private String modifiedAt;

    public SummaryBrief(SummaryDto summaryDto) {
        this.content = summaryDto.getContent();
        this.modified = summaryDto.isModified();
        this.modifiedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(summaryDto.getUpdatedAt());
    }
}
