package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.mapper.Playlist;

import java.util.Optional;
import java.util.Set;

public interface PlaylistRepository {

    Playlist save(Playlist playlist);

    Optional<Playlist> findByCode(String code);

    Playlist updateById(Long playlistId, Playlist playlist);

    void updateDurationById(Long playlistId, int duration);

    Set<String> getCodeListByMemberId(Long memberId);
}
