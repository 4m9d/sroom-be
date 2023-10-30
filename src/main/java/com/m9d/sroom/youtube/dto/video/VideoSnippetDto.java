package com.m9d.sroom.youtube.dto.video;

import com.m9d.sroom.youtube.dto.global.ThumbnailDto;
import lombok.Data;

@Data
public class VideoSnippetDto {
    private String publishedAt;
    private String title;
    private String description;
    private ThumbnailDto thumbnails;
    private String channelTitle;
    private String defaultAudioLanguage;
}
