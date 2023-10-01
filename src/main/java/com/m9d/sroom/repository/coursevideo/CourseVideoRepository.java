package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.global.mapper.CourseVideo;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.VideoWatchInfo;

import java.util.List;
import java.util.Optional;

public interface CourseVideoRepository {

    CourseVideo save(CourseVideo courseVideo);

    CourseVideo updateById(Long id, CourseVideo courseVideo);

    CourseVideo getById(Long courseVideoId);

    List<CourseVideo> getListByCourseId(Long courseId);

    List<CourseVideo> getListByLectureId(Long lectureId);

    Optional<CourseVideo> findById(Long courseVideoId);

    Integer countByCourseId(Long courseId);

    Integer countCompletedByCourseId(Long courseId);

    void deleteByCourseId(Long courseId);

    Long getIdByCourseIdAndPrevIndex(Long courseId, int videoIndex);

    LastVideoInfo getLastByCourseId(Long courseId);

    LastVideoInfo getLastInfoByCourseId(Long courseId);

    List<VideoWatchInfo> getWatchInfoListByCourseIdAndSection(Long courseId, int section);

    List<VideoInfoForSchedule> getInfoForScheduleByCourseId(Long courseId);

    void updateSummaryId(Long videoId, long summaryId);
}
