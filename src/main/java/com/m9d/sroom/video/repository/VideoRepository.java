package com.m9d.sroom.video.repository;

import com.m9d.sroom.video.VideoDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoRepository {

    VideoDto save(VideoDto videoDto);

    VideoDto getByCode(String videoCode);

    VideoDto getById(Long videoId);

    Optional<VideoDto> findByCode(String videoCode);

    List<VideoDto> getTopRatedOrder(int limit);

    Optional<VideoDto> findById(Long videoId);

    VideoDto updateById(Long videoId, VideoDto videoDto);

    List<VideoDto> getListByPlaylistId(Long playlistId);

    Set<String> getCodeSetByMemberId(Long memberId);

    List<VideoDto> getRandomByChannel(String channel, int limit);

    List<VideoDto> getViewCountOrderByChannel(String channel, int limit);

    List<VideoDto> getLatestOrderByChannel(String channel, int limit);
}
