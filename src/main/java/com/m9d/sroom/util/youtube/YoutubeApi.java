package com.m9d.sroom.util.youtube;

import com.m9d.sroom.util.youtube.resource.LectureListReq;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchItemVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import reactor.core.publisher.Mono;

public interface YoutubeApi {

    Mono<SearchItemVo> getSearchVo(LectureListReq lectureListReq);

    VideoVo getVideoVo(VideoReq videoReq);

    PlaylistVo getPlaylist(PlaylistReq playlistReq);

    PlaylistVideoVo getPlaylistVideo(PlaylistItemReq playlistItemReq);

}
