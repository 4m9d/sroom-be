package com.m9d.sroom.util.youtube;

import com.m9d.sroom.util.youtube.resource.*;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchItemVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import reactor.core.publisher.Mono;

public interface YoutubeApi {

    Mono<String> getYoutubeVoStr(YoutubeResource resource);

}
