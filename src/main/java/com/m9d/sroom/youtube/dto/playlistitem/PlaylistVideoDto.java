package com.m9d.sroom.youtube.dto.playlistitem;

import com.m9d.sroom.youtube.vo.PlaylistItemInfo;
import com.m9d.sroom.youtube.vo.PlaylistVideoInfo;
import com.m9d.sroom.youtube.dto.global.ContentDto;
import com.m9d.sroom.youtube.dto.global.PageInfoDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class PlaylistVideoDto extends ContentDto {

    private PageInfoDto pageInfo;

    private String nextPageToken;

    private List<PlaylistVideoItemDto> items;

    public PlaylistItemInfo toPlaylistItemInfo(){
        return PlaylistItemInfo.builder()
                .nextPageToken(nextPageToken)
                .totalResultCount(pageInfo.getTotalResults())
                .resultPerPage(pageInfo.getResultsPerPage())
                .playlistVideoInfoList(getPlaylistVideoInfoList())
                .build();
    }

    private List<PlaylistVideoInfo> getPlaylistVideoInfoList() {
        List<PlaylistVideoInfo> playlistVideoInfoList = new ArrayList<>();

        for (PlaylistVideoItemDto itemVo : items) {
            playlistVideoInfoList.add(itemVo.toPlaylistVideoInfo());
        }
        return playlistVideoInfoList;
    }
}
