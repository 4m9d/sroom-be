package com.m9d.sroom.util.youtube.vo.playlistitem;

import com.m9d.sroom.util.youtube.vo.global.ThumbnailVo;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistVideoSnippetVo {
    private String title;
    private int position;
    private PlaylistVideoResourceIdVo resourceId;
    private ThumbnailVo thumbnails;
}
