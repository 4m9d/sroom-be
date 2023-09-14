package com.m9d.sroom.repository.summary;

import com.m9d.sroom.global.mapper.Summary;

import java.util.Optional;

public interface SummaryRepository {

    void save(Summary summary);

    Optional<Summary> findByCourseVideoId(Long courseVideoId);

    void update(Summary summary);
}
