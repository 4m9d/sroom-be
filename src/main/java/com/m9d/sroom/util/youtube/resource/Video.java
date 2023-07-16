package com.m9d.sroom.util.youtube.resource;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import com.m9d.sroom.util.youtube.YoutubeApiParameters;

import java.util.Map;

@RequiredArgsConstructor
public class Video implements YoutubeResource {

    private final String videoId;
    private static final String ENDPOINT = "https://www.googleapis.com/youtube/v3/videos?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeApiParameters.VIDEO_PARAMETERS);
        params.put("id", videoId);
        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}
