package com.m9d.sroom.common.repository.playlist;
import com.m9d.sroom.common.entity.PlaylistEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {

    PlaylistEntity save(PlaylistEntity playlist);

    PlaylistEntity getById(Long playlistId);

    Optional<PlaylistEntity> findByCode(String code);

    PlaylistEntity getByCode(String code);

    PlaylistEntity updateById(Long playlistId, PlaylistEntity playlist);

    List<PlaylistEntity> getTopRatedOrder(int limit);

    HashSet<String> getCodeSetByMemberId(Long memberId);

    List<PlaylistEntity> getRandomByChannel(String channel, int limit);

    List<PlaylistEntity> getViewCountOrderByChannel(String channel, int limit);

    List<PlaylistEntity> getLatestOrderByChannel(String channel, int limit);
}
