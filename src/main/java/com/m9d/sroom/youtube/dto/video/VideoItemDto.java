package com.m9d.sroom.youtube.dto.video;

import lombok.Data;

@Data
public class VideoItemDto {

    private String id;
    private VideoSnippetDto snippet;
    private VideoContentDetailsDto contentDetails;
    private VideoStatusDto status;
    private VideoStatisticsDto statistics;

}
