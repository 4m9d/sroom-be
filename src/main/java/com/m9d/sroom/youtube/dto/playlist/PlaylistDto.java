package com.m9d.sroom.youtube.dto.playlist;

import com.m9d.sroom.playlist.vo.Playlist;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.dto.global.ContentDto;
import com.m9d.sroom.youtube.dto.global.PageInfoDto;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.List;

import static com.m9d.sroom.youtube.YoutubeConstant.FIRST_INDEX;

@Getter
public class PlaylistDto extends ContentDto {
    private PageInfoDto pageInfo;
    private List<PlaylistItemDto> items;

    public Playlist toPlaylist(int reviewCount, int accumulatedRating) {
        PlaylistItemDto itemVo = items.get(FIRST_INDEX);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        return Playlist.builder()
                .code(items.get(0).getId())
                .title(itemVo.getSnippet().getTitle())
                .channel(itemVo.getSnippet().getChannelTitle())
                .thumbnail(selectThumbnailInVo(itemVo.getSnippet().getThumbnails()))
                .description(itemVo.getSnippet().getDescription())
                .publishedAt(DateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt()))
                .videoCount(itemVo.getContentDetails().getItemCount())
                .reviewCount(reviewCount)
                .rating(Double.parseDouble(decimalFormat.format((double) accumulatedRating
                        / reviewCount)))
                .build();
    }
}
