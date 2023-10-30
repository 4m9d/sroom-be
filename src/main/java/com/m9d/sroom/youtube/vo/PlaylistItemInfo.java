package com.m9d.sroom.youtube.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class PlaylistItemInfo {

    private final String nextPageToken;

    private final Integer totalResultCount;

    private final Integer resultPerPage;

    private final List<PlaylistVideoInfo> playlistVideoInfoList;

}
