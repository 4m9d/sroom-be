package com.m9d.sroom.youtube.dto.search;

import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.vo.SearchItemInfo;
import com.m9d.sroom.youtube.dto.global.ContentDto;
import lombok.Getter;

import static com.m9d.sroom.youtube.YoutubeConstant.*;

@Getter
public class SearchItemDto extends ContentDto {
    private SearchIdDto id;
    private SearchSnippetDto snippet;

    public SearchItemInfo toSearchItemInfo() {
        String contentCode;
        boolean isPlaylist = false;
        if (id.getKind().equals(JSONNODE_TYPE_PLAYLIST)) {
            contentCode = id.getPlaylistId();
            isPlaylist = true;
        } else if (id.getKind().equals(JSONNODE_TYPE_VIDEO)) {
            contentCode = id.getVideoId();
        } else {
            contentCode = "KNOWN_ITEM_CODE";
        }

        return SearchItemInfo.builder()
                .kind(id.getKind())
                .isPlaylist(isPlaylist)
                .code(contentCode)
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .channelId(snippet.getChannelId())
                .channel(snippet.getChannelTitle())
                .publishedAt(DateUtil.convertISOToTimestamp(snippet.getPublishTime()))
                .thumbnail(selectThumbnailInVo(snippet.getThumbnails()))
                .build();
    }
}
