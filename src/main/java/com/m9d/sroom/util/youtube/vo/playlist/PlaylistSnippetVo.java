package com.m9d.sroom.util.youtube.vo.playlist;

import com.m9d.sroom.util.youtube.vo.global.ThumbnailVo;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistSnippetVo {
    private String publishedAt;
    private String title;
    private String description;
    private ThumbnailVo thumbnails;
    private String channelTitle;
}
