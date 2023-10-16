package com.m9d.sroom.youtube.resource;

import java.util.Map;

public abstract class YoutubeReq {
    protected String endPoint;

    public abstract Map<String, String> getParameters();

    public String getEndPoint() {
        return endPoint;
    }
}
