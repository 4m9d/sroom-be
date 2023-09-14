package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.mapper.Video;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoRepository {

    Long save(Video video);

    Video getByCode(String videoCode);

    Optional<Video> findByCode(String videoCode);

    Optional<Video> findById(Long videoId);

    Long update(Video video);

    void updateMaterialStatusByCode(String videoCode, int materialStatus);

    Set<String> getCodeListByMemberId(Long memberId);

    List<Video> getListByPlaylistId(Long playlistId);
}
