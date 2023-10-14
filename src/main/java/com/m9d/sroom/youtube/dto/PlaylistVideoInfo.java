package com.m9d.sroom.youtube.dto;

import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoItemVo;
import lombok.Getter;

@Getter
public class PlaylistVideoInfo extends ContentInfo{

    private final String title;

    private final Integer position;

    private final String thumbnail;

    private final String videoCode;

    private final String privacyStatus;


    public PlaylistVideoInfo(PlaylistVideoItemVo playlistVideoItemVo) {
        this.title = playlistVideoItemVo.getSnippet().getTitle();
        this.position = playlistVideoItemVo.getSnippet().getPosition();
        this.thumbnail = selectThumbnailInVo(playlistVideoItemVo.getSnippet().getThumbnails());
        this.videoCode = playlistVideoItemVo.getSnippet().getResourceId().getVideoId();
        this.privacyStatus = playlistVideoItemVo.getStatus().getPrivacyStatus();
    }
}
