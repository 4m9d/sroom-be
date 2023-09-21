package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.mapper.Playlist;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class PlaylistJdbcRepositoryImpl implements PlaylistRepository{
    @Override
    public Playlist save(Playlist playlist) {
        return null;
    }

    @Override
    public Optional<Playlist> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public Playlist updateById(Long playlistId, Playlist playlist) {
        return null;
    }

    @Override
    public void updateDurationById(Long playlistId, int duration) {

    }

    @Override
    public Set<String> getCodeListByMemberId(Long memberId) {
        return null;
    }
}
