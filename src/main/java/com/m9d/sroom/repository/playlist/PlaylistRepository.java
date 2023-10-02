package com.m9d.sroom.repository.playlist;
import com.m9d.sroom.global.mapper.Playlist;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlaylistRepository {

    Playlist save(Playlist playlist);

    Playlist getById(Long playlistId);

    Optional<Playlist> findByCode(String code);

    Playlist updateById(Long playlistId, Playlist playlist);

    List<Playlist> getTopRatedOrder(int limit);

    void updateDurationById(Long playlistId, int duration);

    HashSet<String> getCodeSetByMemberId(Long memberId);
    Set<String> getCodeListByMemberId(Long memberId);

    List<Playlist> getRandomByChannel(String channel, int limit);

    List<Playlist> getViewCountOrderByChannel(String channel, int limit);

    List<Playlist> getLatestOrderByChannel(String channel, int limit);
}
