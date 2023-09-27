package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.global.mapper.CourseVideo;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;

import java.util.List;
import java.util.Optional;

public interface CourseVideoRepository {

    void save(CourseVideo courseVideo);

    void updateById(Long id, CourseVideo courseVideo);

    CourseVideo getById(Long courseVideoId);

    List<CourseVideo> getListByCourseId(Long courseId);

    Optional<CourseVideo> findById(Long courseVideoId);
    Integer countByCourseId(Long courseId);

    Integer countCompletedByCourseId(Long courseId);

    Long getIdByCourseIdAndPrevIndex(Long courseId, int videoIndex);

    LastVideoInfo getLastByCourseId(Long courseId);
}