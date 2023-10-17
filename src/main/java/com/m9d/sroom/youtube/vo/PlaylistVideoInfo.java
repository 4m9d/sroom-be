package com.m9d.sroom.youtube.vo;

import com.m9d.sroom.youtube.YoutubeConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PlaylistVideoInfo {

    private final String title;

    private final Integer position;

    private final String thumbnail;

    private final String videoCode;

    private final String privacyStatus;

    public boolean isPrivacyStatusUnusable() {
        return privacyStatus.equals(YoutubeConstant.JSONNODE_PRIVATE) || privacyStatus.equals(YoutubeConstant.JSONNODE_UNSPECIFIED);
    }
}
