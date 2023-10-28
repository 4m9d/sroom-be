package com.m9d.sroom.youtube.resource;

import com.m9d.sroom.youtube.YoutubeConstant;
import lombok.Builder;

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
        Map<String, String> params = new HashMap<>(YoutubeConstant.VIDEO_PARAMETERS);
        params.put("id", videoCode);
        return params;
    }
}
