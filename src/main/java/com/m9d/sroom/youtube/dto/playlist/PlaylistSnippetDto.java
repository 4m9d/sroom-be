package com.m9d.sroom.youtube.dto.playlist;

import com.m9d.sroom.youtube.dto.global.ThumbnailDto;
import lombok.Data;

@Data
public class PlaylistSnippetDto {
    private String publishedAt;
    private String title;
    private String description;
    private ThumbnailDto thumbnails;
    private String channelTitle;
}
