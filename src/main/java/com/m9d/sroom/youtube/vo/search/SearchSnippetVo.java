package com.m9d.sroom.youtube.vo.search;

import com.m9d.sroom.youtube.vo.common.ThumbnailVo;
import lombok.Data;

@Data
public class SearchSnippetVo {
    private String publishTime;
    private String channelId;
    private String title;
    private String description;
    private ThumbnailVo thumbnails;
    private String channelTitle;
}
