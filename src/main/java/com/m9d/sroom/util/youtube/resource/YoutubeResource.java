package com.m9d.sroom.util.youtube.resource;

import java.util.Map;

public interface YoutubeResource {
    Map<String, String> getParameters();

    String getEndpoint();
}
