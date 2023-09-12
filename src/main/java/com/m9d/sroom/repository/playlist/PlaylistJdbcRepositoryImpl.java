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
    public void update(Playlist playlist) {

    }

    @Override
    public void updateDurationById(Long playlistId, int duration) {

    }

    @Override
    public Set<String> getCodeListByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Optional<PlaylistInfoInSearch> findPlaylistInfoSearch(String playlistCode) {
        return Optional.empty();
    }

    @Override
    public List<RecommendLecture> getPlaylistsSortedByRating() {
        return null;
    }

    @Override
    public List<RecommendLecture> getRandomListByChannel(String channel, int limit) {
        return null;
    }

    @Override
    public List<RecommendLecture> getMostViewedListByChannel(String channel, int limit) {
        return null;
    }

    @Override
    public List<RecommendLecture> getLatestListByChannel(String channel, int limit) {
        return null;
    }
}
