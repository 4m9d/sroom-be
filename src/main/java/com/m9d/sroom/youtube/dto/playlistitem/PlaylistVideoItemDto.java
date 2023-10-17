package com.m9d.sroom.youtube.dto.playlistitem;

import com.m9d.sroom.youtube.vo.PlaylistVideoInfo;
import com.m9d.sroom.youtube.dto.global.ContentDto;
import lombok.Getter;

@Getter
public class PlaylistVideoItemDto extends ContentDto {
    private PlaylistVideoSnippetDto snippet;
    private PlaylistVideoStatusDto status;

    public PlaylistVideoInfo toPlaylistVideoInfo() {
        return PlaylistVideoInfo.builder()
                .title(snippet.getTitle())
                .position(snippet.getPosition())
                .thumbnail(selectThumbnailInVo(snippet.getThumbnails()))
                .videoCode(snippet.getResourceId().getVideoId())
                .privacyStatus(status.getPrivacyStatus())
                .build();
    }
}
