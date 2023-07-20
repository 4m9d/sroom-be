package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeApiParameters;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class LectureList implements YoutubeResource {
    private final String keyword;
    private final int limit;
    private final String filter;
    private final String pageToken;
    private final String type;
    private static final String ENDPOINT = "https://www.googleapis.com/youtube/v3/search?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeApiParameters.LECTURE_LIST_PARAMETERS);
        params.put("maxResults", String.valueOf(limit));
        if (filter.equals("all")) {
            params.put("type", "playlist,video");
        } else {
            params.put("type", filter);
        }
        params.put("q", keyword);

        if (pageToken != null) {
            params.put("pageToken", pageToken);
        }

        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}