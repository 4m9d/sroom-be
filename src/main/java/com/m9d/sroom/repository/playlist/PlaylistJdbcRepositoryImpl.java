package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.model.Playlist;
import com.m9d.sroom.lecture.dto.PlaylistInfoInSearch;
import com.m9d.sroom.lecture.dto.response.RecommendLecture;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PlaylistJdbcRepositoryImpl implements PlaylistRepository{
    @Override
    public Long save(Playlist playlist) {
        return null;
    }

    @Override
    public Optional<Playlist> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public Long updateById(Long playlistId, Playlist playlist) {
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
