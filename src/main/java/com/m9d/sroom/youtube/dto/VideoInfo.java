package com.m9d.sroom.youtube.dto;

import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.vo.video.VideoItemVo;
import com.m9d.sroom.youtube.vo.video.VideoVo;
import lombok.Getter;

import java.sql.Timestamp;

import static com.m9d.sroom.youtube.YoutubeConstant.*;

@Getter
public class VideoInfo extends ContentInfo {

    private final String code;

    private final String title;

    private final String channel;

    private final String thumbnail;

    private final String description;

    private final Integer duration;

    private final Long viewCount;

    private final Timestamp publishedAt;

    private final String language;

    private final String license;

    private final Boolean membership;

    public VideoInfo(VideoVo videoVo) {
        VideoItemVo itemVo = videoVo.getItems().get(FIRST_INDEX);

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

        this.code = itemVo.getId();
        this.title = itemVo.getSnippet().getTitle();
        this.channel = itemVo.getSnippet().getChannelTitle();
        this.thumbnail = selectThumbnailInVo(itemVo.getSnippet().getThumbnails());
        this.description = itemVo.getSnippet().getDescription();
        this.duration = DateUtil.convertISOToSeconds(itemVo.getContentDetails().getDuration());
        this.viewCount = viewCount;
        this.publishedAt = DateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt());
        this.language = language;
        this.license = itemVo.getStatus().getLicense();
        this.membership = membership;
    }
}
