package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.mapper.Video;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class VideoJdbcRepositoryImpl implements VideoRepository{
    @Override
    public Long save(Video video) {
        return null;
    }

    @Override
    public Video getByCode(String videoCode) {
        return null;
    }

    @Override
    public Optional<Video> findByCode(String videoCode) {
        return Optional.empty();
    }

    @Override
    public Optional<Video> findById(Long videoId) {
        return Optional.empty();
    }

    @Override
    public Long update(Video video) {
        return null;
    }

    @Override
    public void updateMaterialStatusByCode(String videoCode, int materialStatus) {

    }

    @Override
    public Set<String> getCodeListByMemberId(Long memberId) {
        return null;
    }

    @Override
    public List<Video> getListByPlaylistId(Long playlistId) {
        return null;
    }
}
