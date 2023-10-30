package com.m9d.sroom.youtube.dto.playlistitem;

import com.m9d.sroom.youtube.dto.global.ThumbnailDto;
import lombok.Data;

@Data
public class PlaylistVideoSnippetDto {
    private String title;
    private int position;
    private PlaylistVideoResourceIdDto resourceId;
    private ThumbnailDto thumbnails;
}
