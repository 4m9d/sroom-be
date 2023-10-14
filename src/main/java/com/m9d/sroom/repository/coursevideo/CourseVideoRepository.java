package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.global.mapper.CourseVideoDto;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.VideoWatchInfo;

import java.util.List;
import java.util.Optional;

public interface CourseVideoRepository {

    CourseVideoDto save(CourseVideoDto courseVideoDto);

    CourseVideoDto updateById(Long id, CourseVideoDto courseVideoDto);

    CourseVideoDto getById(Long courseVideoId);

    List<CourseVideoDto> getListByCourseId(Long courseId);

    List<CourseVideoDto> getListByLectureId(Long lectureId);

    Optional<CourseVideoDto> findById(Long courseVideoId);

    Integer countByCourseId(Long courseId);

    Integer countCompletedByCourseId(Long courseId);

    void deleteByCourseId(Long courseId);

    Optional<CourseVideoDto> findByCourseIdAndPrevIndex(Long courseId, int videoIndex);

    LastVideoInfo getLastInfoByCourseId(Long courseId);

    List<VideoWatchInfo> getWatchInfoListByCourseIdAndSection(Long courseId, int section);

    List<VideoInfoForSchedule> getInfoForScheduleByCourseId(Long courseId);

    void updateSummaryId(Long videoId, long summaryId);
}
