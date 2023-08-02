package com.m9d.sroom.util.youtube.vo.video;

import lombok.Data;

@Data
public class VideoItemVo {

    private String id;
    private VideoSnippetVo snippet;
    private VideoContentDetailsVo contentDetails;
    private VideoStatusVo status;
    private VideoStatisticsVo statistics;

}
