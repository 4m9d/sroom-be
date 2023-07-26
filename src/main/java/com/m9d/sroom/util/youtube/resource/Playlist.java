package com.m9d.sroom.util.youtube.resource;

import com.m9d.sroom.util.youtube.YoutubeConstant;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class Playlist implements YoutubeResource {

    private final String playlistId;

    private static final String ENDPOINT = "playlists?";

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<>(YoutubeConstant.PLAYLIST_PARAMETERS);
        params.put("id", playlistId);
        return params;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }
}
