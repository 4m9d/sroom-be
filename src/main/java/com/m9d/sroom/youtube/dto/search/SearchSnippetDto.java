package com.m9d.sroom.youtube.dto.search;

import com.m9d.sroom.youtube.dto.global.ThumbnailDto;
import lombok.Data;

@Data
public class SearchSnippetDto {
    private String publishTime;
    private String channelId;
    private String title;
    private String description;
    private ThumbnailDto thumbnails;
    private String channelTitle;
}
