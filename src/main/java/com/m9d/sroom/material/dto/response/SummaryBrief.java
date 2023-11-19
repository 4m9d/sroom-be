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

    private Long id;

    private String content;

    @JsonProperty("is_modified")
    private boolean modified;

    private String modifiedAt;

    private FeedbackInfo feedbackInfo;

    public SummaryBrief(SummaryEntity summaryEntity, FeedbackInfo feedbackInfo) {
        this.id = summaryEntity.getId();
        this.content = summaryEntity.getContent();
        this.modified = summaryEntity.isModified();
        this.modifiedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(summaryEntity.getUpdatedAt());
        this.feedbackInfo = feedbackInfo;
        if(modified){
            this.feedbackInfo.setAvailable(false);
        }
    }

    public SummaryBrief(SummaryEntity summaryEntity) {
        this.id = summaryEntity.getId();
        this.content = summaryEntity.getContent();
        this.modified = summaryEntity.isModified();
        this.modifiedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(summaryEntity.getUpdatedAt());
    }
}
