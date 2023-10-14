package com.m9d.sroom.util.youtube.dto;

import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class PlaylistItemInfo {

    private final String nextPageToken;

    private final Integer totalResultCount;

    private final Integer resultPerPage;

    private final List<PlaylistVideoInfo> playlistVideoInfoList;

    public PlaylistItemInfo(PlaylistVideoVo playlistVideoVo) {
        this.nextPageToken = Optional.of(playlistVideoVo)
                .map(PlaylistVideoVo::getNextPageToken)
                .orElse(null);
        this.playlistVideoInfoList = playlistVideoVo.convertToInfoList();
        this.totalResultCount = playlistVideoVo.getPageInfo().getTotalResults();
        this.resultPerPage = playlistVideoVo.getPageInfo().getResultsPerPage();
    }
}
