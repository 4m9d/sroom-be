package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.model.Playlist;

import java.util.Optional;

public interface PlaylistRepository {

    Long save(Playlist playlist);

    Optional<Playlist> findByCode(String code);

    void update(Playlist playlist);

    void updateDurationById(Long playlistId, int duration);


}
