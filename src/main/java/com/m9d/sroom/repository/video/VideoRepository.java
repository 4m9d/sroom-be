package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.mapper.Video;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoRepository {

    Video save(Video video);

    Video getByCode(String videoCode);

    Optional<Video> findByCode(String videoCode);

    Optional<Video> findById(Long videoId);

    Video updateById(Long videoId, Video video);

    List<Video> getListByPlaylistId(Long playlistId);
}
