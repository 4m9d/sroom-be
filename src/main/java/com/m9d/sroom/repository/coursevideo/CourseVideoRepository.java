package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.course.dto.EnrolledCourseVideo;
import com.m9d.sroom.course.dto.SchedulingVideo;
import com.m9d.sroom.global.model.CourseVideo;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.material.model.CourseVideoKey;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface CourseVideoRepository {

    void save(CourseVideo courseVideo);

    Optional<CourseVideo> findCourseVideoById(Long courseVideoId);

    Integer countByCourseId(Long courseId);

    Integer countCompletedByCourseId(Long courseId);

    List<EnrolledCourseVideo> getEnrolledCourseVideoListByCourseId(Long courseId);

    List<SchedulingVideo> getSchedulingVideoByCourseId(Long courseId);

    void updateSectionById(Long courseVideoId, int section);

    LastVideoInfo getLastVideoInfoByCourseId(Long courseId);

    void updateViewingStatus(CourseVideo courseVideo);

    CourseVideoKey getCourseVideoKeyById(Long courseVideoId);

    Long getIdByCourseIdAndPrevIndex(Long courseId, int videoIndex);

    void updateLastViewTimeById(Long courseVideoId, Timestamp time);

    void updateSummaryIdByVideoId(Long videoId, Long summaryId);



}
