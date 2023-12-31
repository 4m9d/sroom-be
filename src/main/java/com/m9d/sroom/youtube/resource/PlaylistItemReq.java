package com.m9d.sroom.youtube.resource;

import com.m9d.sroom.youtube.YoutubeConstant;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public class PlaylistItemReq extends YoutubeReq {

    private final String playlistCode;
    private final String nextPageToken;
    private final int limit;

    {
        endPoint = "/playlistItems";
    }

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
}
