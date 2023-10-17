package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.common.entity.SummaryEntity;
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

    public SummaryBrief(SummaryEntity summary) {
        this.content = summary.getContent();
        this.modified = summary.isModified();
        this.modifiedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(summary.getUpdatedAt());
    }
}
