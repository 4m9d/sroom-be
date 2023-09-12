package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.model.Playlist;
import com.m9d.sroom.lecture.dto.PlaylistInfoInSearch;
import com.m9d.sroom.lecture.dto.response.RecommendLecture;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlaylistRepository {

    Long save(Playlist playlist);

    Optional<Playlist> findByCode(String code);

    void update(Playlist playlist);

    void updateDurationById(Long playlistId, int duration);

    Set<String> getCodeListByMemberId(Long memberId);
}
