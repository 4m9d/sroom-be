package com.m9d.sroom.course.dto;

import lombok.Data;

@Data
public class InnerContent {

    private final Long contentId;

    private final Long summaryId;

    private final Integer duration;
}
