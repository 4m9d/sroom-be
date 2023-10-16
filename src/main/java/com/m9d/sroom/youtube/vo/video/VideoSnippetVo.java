package com.m9d.sroom.youtube.vo.video;

import com.m9d.sroom.youtube.vo.global.ThumbnailVo;
import lombok.Data;

@Data
public class VideoSnippetVo {
    private String publishedAt;
    private String title;
    private String description;
    private ThumbnailVo thumbnails;
    private String channelTitle;
    private String defaultAudioLanguage;
}
