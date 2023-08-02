package com.m9d.sroom.util.youtube.vo.video;

import com.m9d.sroom.util.youtube.vo.global.ThumbnailVo;
import lombok.Data;

import java.util.List;

@Data
public class VideoSnippetVo {
    private String publishedAt;
    private String title;
    private String description;
    private ThumbnailVo thumbnails;
    private String channelTitle;
    private String defaultAudioLanguage;
}
