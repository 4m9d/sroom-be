package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeConstant;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class LectureListReq implements YoutubeResource {

    private final String keyword;
    private final int limit;
    private final String filter;
    private final String pageToken;
    private final String type;

    private static final String ENDPOINT = "search?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeConstant.LECTURE_LIST_PARAMETERS);
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