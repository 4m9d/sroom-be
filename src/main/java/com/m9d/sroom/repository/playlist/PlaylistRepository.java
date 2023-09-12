package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.model.Playlist;

import java.util.Optional;
import java.util.Set;

public interface PlaylistRepository {

    Long save(Playlist playlist);

    Optional<Playlist> findByCode(String code);

    Long updateById(Long playlistId, Playlist playlist);

    void updateDurationById(Long playlistId, int duration);

    Set<String> getCodeListByMemberId(Long memberId);
}
