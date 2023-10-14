package com.m9d.sroom.youtube.vo.playlistitem;

import com.m9d.sroom.youtube.dto.PlaylistVideoInfo;
import com.m9d.sroom.youtube.vo.common.PageInfoVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlaylistVideoVo {

    private PageInfoVo pageInfo;

    private String nextPageToken;

    private List<PlaylistVideoItemVo> items;

    public List<PlaylistVideoInfo> convertToInfoList() {
        List<PlaylistVideoInfo> playlistVideoInfoList = new ArrayList<>();

        for (PlaylistVideoItemVo itemVo : items) {
            playlistVideoInfoList.add(new PlaylistVideoInfo(itemVo));
        }
        return playlistVideoInfoList;
    }
}
