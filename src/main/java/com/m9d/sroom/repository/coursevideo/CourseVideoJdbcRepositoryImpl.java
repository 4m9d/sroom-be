package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.global.mapper.CourseVideo;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseVideoJdbcRepositoryImpl implements CourseVideoRepository{
    @Override
    public void save(CourseVideo courseVideo) {

    }

    @Override
    public void updateById(Long id, CourseVideo courseVideo) {

    }

    @Override
    public CourseVideo getById(Long courseVideoId) {
        return null;
    }

    @Override
    public List<CourseVideo> getListByCourseId(Long courseId) {
        return null;
    }

    @Override
    public Optional<CourseVideo> findById(Long courseVideoId) {
        return Optional.empty();
    }

    @Override
    public Integer countByCourseId(Long courseId) {
        return null;
    }

    @Override
    public Integer countCompletedByCourseId(Long courseId) {
        return null;
    }

    @Override
    public Long getIdByCourseIdAndPrevIndex(Long courseId, int videoIndex) {
        return null;
    }

    @Override
    public LastVideoInfo getLastByCourseId(Long courseId) {
        return null;
    }
}