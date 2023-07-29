package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeConstant;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class PlaylistItemReq implements YoutubeResource {

    private final String playlistCode;
    private final String nextPageToken;
    private final int limit;

    private static final String ENDPOINT = "playlistItems?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeConstant.PLAYLIST_ITEMS_PARAMETERS);
        params.put("playlistId", playlistCode);
        params.put("maxResults", String.valueOf(limit));
        if (nextPageToken != null) {
            params.put("pageToken", nextPageToken);
        }
        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}
