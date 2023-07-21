package com.m9d.sroom.util.youtube.resource;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import com.m9d.sroom.util.youtube.YoutubeConstant;

import java.util.Map;

@Builder
@RequiredArgsConstructor
public class Video implements YoutubeResource {

    private final String videoId;
    private static final String ENDPOINT = "https://www.googleapis.com/youtube/v3/videos?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeConstant.VIDEO_PARAMETERS);
        params.put("id", videoId);
        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}
