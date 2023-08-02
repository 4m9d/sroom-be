package com.m9d.sroom.util.youtube.vo.search;

import com.m9d.sroom.util.youtube.vo.global.ThumbnailVo;
import lombok.Data;

import java.util.List;

@Data
public class SearchSnippetVo {
    private String publishedAt;
    private String channelId;
    private String title;
    private String description;
    private ThumbnailVo thumbnails;
    private String chanelTitle;
}
