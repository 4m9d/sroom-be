package com.m9d.sroom.common.repository.playlist;
import com.m9d.sroom.common.dto.Playlist;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {

    Playlist save(Playlist playlist);

    Playlist getById(Long playlistId);

    Optional<Playlist> findByCode(String code);

    Playlist updateById(Long playlistId, Playlist playlist);

    List<Playlist> getTopRatedOrder(int limit);

    HashSet<String> getCodeSetByMemberId(Long memberId);

    List<Playlist> getRandomByChannel(String channel, int limit);

    List<Playlist> getViewCountOrderByChannel(String channel, int limit);

    List<Playlist> getLatestOrderByChannel(String channel, int limit);
}
