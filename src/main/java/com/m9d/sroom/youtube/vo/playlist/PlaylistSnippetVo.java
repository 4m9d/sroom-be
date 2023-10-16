package com.m9d.sroom.youtube.vo.playlist;

import com.m9d.sroom.youtube.vo.global.ThumbnailVo;
import lombok.Data;

@Data
public class PlaylistSnippetVo {
    private String publishedAt;
    private String title;
    private String description;
    private ThumbnailVo thumbnails;
    private String channelTitle;
}
