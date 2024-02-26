package com.m9d.sroom.summary;

import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import com.m9d.sroom.material.dto.response.FeedbackInfo;
import com.m9d.sroom.material.dto.response.SummaryBrief;

import java.text.SimpleDateFormat;

public class SummaryMapper {

    public static SummaryBrief getBrief(SummaryEntity summaryEntity, FeedbackInfo feedbackInfo){
        return SummaryBrief.builder()
                .id(summaryEntity.getSummaryId())
                .content(summaryEntity.getContent())
                .modified(summaryEntity.isModified())
                .modifiedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(summaryEntity.getUpdatedTime()))
                .feedbackInfo(feedbackInfo)
                .build();
    }
}
