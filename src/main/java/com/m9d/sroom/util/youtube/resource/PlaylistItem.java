package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeApiParameters;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class PlaylistItem implements YoutubeResource {
    private final String playlistId;
    private final String nextPageToken;
    private final int limit;

    //절이기, 엔드포인트 ..
    private static final String ENDPOINT = "https://www.googleapis.com/youtube/v3/playlistItems?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeApiParameters.PLAYLIST_ITEMS_PARAMETERS);
        params.put("playlistId", playlistId);
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
