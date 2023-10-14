package com.m9d.sroom.util.youtube.dto;

import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistItemVo;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import lombok.Getter;

import java.sql.Timestamp;

import static com.m9d.sroom.util.youtube.YoutubeConstant.FIRST_INDEX;

@Getter
public class PlaylistInfo extends ContentInfo {

    private final String code;

    private final String title;

    private final String channel;

    private final String thumbnail;

    private final String description;

    private final Timestamp publishedAt;

    private final Integer videoCount;

    public PlaylistInfo(PlaylistVo playlistVo) {
        PlaylistItemVo itemVo = playlistVo.getItems().get(FIRST_INDEX);

        this.code = itemVo.getId();
        this.title = itemVo.getSnippet().getTitle();
        this.channel = itemVo.getSnippet().getChannelTitle();
        this.thumbnail = selectThumbnailInVo(itemVo.getSnippet().getThumbnails());
        this.description = itemVo.getSnippet().getDescription();
        this.publishedAt = DateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt());
        this.videoCount = itemVo.getContentDetails().getItemCount();
    }
}
