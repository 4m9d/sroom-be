package com.m9d.sroom.youtube.resource;

import com.m9d.sroom.youtube.YoutubeConstant;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public class PlaylistReq extends YoutubeReq {

    private final String playlistCode;

    {
        endPoint = "/playlists";
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeConstant.PLAYLIST_PARAMETERS);
        params.put("id", playlistCode);
        return params;
    }
}
