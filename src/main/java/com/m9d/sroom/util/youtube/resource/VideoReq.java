package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import java.util.Map;

@Builder
public class VideoReq extends YoutubeReq {

    private final String videoCode;

    {
        endPoint = "/videos";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeUtil.VIDEO_PARAMETERS);
        params.put("id", videoCode);
        return params;
    }
}
