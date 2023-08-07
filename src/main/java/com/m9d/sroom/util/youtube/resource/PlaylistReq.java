package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class PlaylistReq implements YoutubeResource {

    private final String playlistCode;

    private static final String ENDPOINT = "/playlists";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeUtil.PLAYLIST_PARAMETERS);
        params.put("id", playlistCode);
        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}
