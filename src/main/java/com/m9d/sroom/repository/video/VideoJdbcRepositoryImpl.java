package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.lecture.dto.response.RecommendLecture;
import com.m9d.sroom.lecture.dto.response.VideoBrief;
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
    public Optional<Video> findByCode(String videoCode) {
        return Optional.empty();
    }

    @Override
    public Optional<Video> findById(Long videoId) {
        return Optional.empty();
    }

    @Override
    public void update(Video video) {

    }

    @Override
    public void updateMaterialStatusByCode(String videoCode) {

    }

    @Override
    public Set<String> getCodeListByMemberId(Long memberId) {
        return null;
    }
}
