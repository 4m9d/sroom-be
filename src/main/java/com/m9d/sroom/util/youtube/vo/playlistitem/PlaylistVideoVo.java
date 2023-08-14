package com.m9d.sroom.util.youtube.vo.playlistitem;

import com.m9d.sroom.util.youtube.vo.global.PageInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistVideoVo {

    private PageInfoVo pageInfo;

    private String nextPageToken;

    private List<PlaylistVideoItemVo> items;
}
