package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.lecture.dto.PlaylistInfoInSearch;
import com.m9d.sroom.lecture.dto.response.RecommendLecture;
import com.m9d.sroom.lecture.dto.response.VideoBrief;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoRepository {

    Long save(Video video);

    Optional<Video> findByCode(String videoCode);

    Optional<Video> findById(Long videoId);

    void update(Video video);

    void updateMaterialStatusByCode(String videoCode);

    Set<String> getCodeListByMemberId(Long memberId);

}
