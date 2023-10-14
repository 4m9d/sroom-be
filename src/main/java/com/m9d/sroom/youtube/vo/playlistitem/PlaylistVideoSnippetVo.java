package com.m9d.sroom.youtube.vo.playlistitem;

import com.m9d.sroom.youtube.vo.common.ThumbnailVo;
import lombok.Data;

@Data
public class PlaylistVideoSnippetVo {
    private String title;
    private int position;
    private PlaylistVideoResourceIdVo resourceId;
    private ThumbnailVo thumbnails;
}
