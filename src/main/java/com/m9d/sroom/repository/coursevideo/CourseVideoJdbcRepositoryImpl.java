package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.course.dto.EnrolledCourseVideo;
import com.m9d.sroom.course.dto.SchedulingVideo;
import com.m9d.sroom.global.model.CourseVideo;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.material.model.CourseVideoKey;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseVideoJdbcRepositoryImpl implements CourseVideoRepository{
    @Override
    public void save(CourseVideo courseVideo) {

    }

    @Override
    public Optional<CourseVideo> findCourseVideoById(Long courseVideoId) {
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
    public void updateSectionById(Long courseVideoId, int section) {

    }

    @Override
    public void updateViewingStatus(CourseVideo courseVideo) {

    }

    @Override
    public Long getIdByCourseIdAndPrevIndex(Long courseId, int videoIndex) {
        return null;
    }

    @Override
    public void updateLastViewTimeById(Long courseVideoId, Timestamp time) {

    }

    @Override
    public void updateSummaryIdByVideoId(Long videoId, Long summaryId) {

    }

    @Override
    public void updateSummaryIdById(Long courseVideoId, Long summaryId) {

    }
}
