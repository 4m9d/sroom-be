package com.m9d.sroom.youtube.dto.video;

import com.m9d.sroom.common.vo.Video;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.dto.global.ContentDto;
import com.m9d.sroom.youtube.dto.global.PageInfoDto;
import lombok.Getter;

import java.util.List;

import static com.m9d.sroom.youtube.YoutubeConstant.*;

@Getter
public class VideoDto extends ContentDto {

    private String nextPageToken;

    private PageInfoDto pageInfo;

    private List<VideoItemDto> items;

    public Video toVideo() {
        VideoItemDto itemVo = items.get(FIRST_INDEX);

        String language;
        if (itemVo.getSnippet().getDefaultAudioLanguage() != null) {
            language = itemVo.getSnippet().getDefaultAudioLanguage();
        } else {
            language = UNKNOWN_LANGUAGE;
        }
        boolean membership = false;
        Long viewCount = itemVo.getStatistics().getViewCount();
        if (viewCount == null) {
            membership = true;
            viewCount = VIEW_COUNT_DEFAULT;
        }

        return Video.builder()
                .code(itemVo.getId())
                .title(itemVo.getSnippet().getTitle())
                .channel(itemVo.getSnippet().getChannelTitle())
                .thumbnail(selectThumbnailInVo(itemVo.getSnippet().getThumbnails()))
                .description(itemVo.getSnippet().getDescription())
                .duration(DateUtil.convertISOToSeconds(itemVo.getContentDetails().getDuration()))
                .viewCount(viewCount)
                .publishedAt(DateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt()))
                .language(language)
                .license(itemVo.getStatus().getLicense())
                .membership(membership)
                .build();
    }

}
