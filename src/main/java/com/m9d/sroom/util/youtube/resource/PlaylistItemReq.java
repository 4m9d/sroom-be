package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

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
        Map<String, String> params = new HashMap<>(YoutubeUtil.PLAYLIST_ITEMS_PARAMETERS);
        params.put("playlistId", playlistCode);
        params.put("maxResults", String.valueOf(limit));
        if (nextPageToken != null) {
            params.put("pageToken", nextPageToken);
        }
        return params;
    }
}
