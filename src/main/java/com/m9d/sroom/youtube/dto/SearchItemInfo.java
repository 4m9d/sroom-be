package com.m9d.sroom.youtube.dto;

import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.vo.search.SearchItemVo;
import lombok.Getter;

import java.sql.Timestamp;

import static com.m9d.sroom.youtube.YoutubeUtil.JSONNODE_TYPE_PLAYLIST;
import static com.m9d.sroom.youtube.YoutubeUtil.JSONNODE_TYPE_VIDEO;

@Getter
public class SearchItemInfo extends ContentInfo {

    private final String kind;

    private final boolean isPlaylist;

    private final String code;

    private final String title;

    private final String description;

    private final String channelId;

    private final String channel;

    private final Timestamp publishedAt;

    private final String thumbnail;

    public SearchItemInfo(SearchItemVo searchItemVo) {
        this.kind = searchItemVo.getId().getKind();

        if (this.kind.equals(JSONNODE_TYPE_PLAYLIST)) {
            this.code = searchItemVo.getId().getPlaylistId();
            this.isPlaylist = true;
        } else if (this.kind.equals(JSONNODE_TYPE_VIDEO)) {
            this.code = searchItemVo.getId().getVideoId();
            this.isPlaylist = false;
        } else {
            this.code = "KNOWN_ITEM_CODE";
            this.isPlaylist = false;
        }

        this.title = searchItemVo.getSnippet().getTitle();
        this.description = searchItemVo.getSnippet().getDescription();
        this.channelId = searchItemVo.getSnippet().getChannelId();
        this.channel = searchItemVo.getSnippet().getChannelTitle();
        this.publishedAt = DateUtil.convertISOToTimestamp(searchItemVo.getSnippet().getPublishTime());
        this.thumbnail = selectThumbnailInVo(searchItemVo.getSnippet().getThumbnails());
    }
}
