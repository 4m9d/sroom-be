package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import java.util.Map;

@Builder
@RequiredArgsConstructor
public class VideoReq implements YoutubeResource {

    private final String videoCode;

    private static final String ENDPOINT = "/videos";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeUtil.VIDEO_PARAMETERS);
        params.put("id", videoCode);
        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}
