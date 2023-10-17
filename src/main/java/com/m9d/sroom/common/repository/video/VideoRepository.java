package com.m9d.sroom.common.repository.video;

import com.m9d.sroom.common.entity.VideoEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoRepository {

    VideoEntity save(VideoEntity video);

    VideoEntity getByCode(String videoCode);

    VideoEntity getById(Long videoId);

    Optional<VideoEntity> findByCode(String videoCode);

    List<VideoEntity> getTopRatedOrder(int limit);

    Optional<VideoEntity> findById(Long videoId);

    VideoEntity updateById(Long videoId, VideoEntity video);

    List<VideoEntity> getListByPlaylistId(Long playlistId);

    Set<String> getCodeSetByMemberId(Long memberId);

    List<VideoEntity> getRandomByChannel(String channel, int limit);

    List<VideoEntity> getViewCountOrderByChannel(String channel, int limit);

    List<VideoEntity> getLatestOrderByChannel(String channel, int limit);
}
