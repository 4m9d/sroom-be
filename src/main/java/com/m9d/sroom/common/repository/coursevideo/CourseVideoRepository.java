package com.m9d.sroom.common.repository.coursevideo;

import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.search.dto.response.VideoInfo;
import com.m9d.sroom.search.dto.response.VideoWatchInfo;

import java.util.List;
import java.util.Optional;

public interface CourseVideoRepository {

    CourseVideoEntity save(CourseVideoEntity courseVideo);

    CourseVideoEntity updateById(Long id, CourseVideoEntity courseVideo);

    CourseVideoEntity getById(Long courseVideoId);

    List<CourseVideoEntity> getListByCourseId(Long courseId);

    List<CourseVideoEntity> getListByLectureId(Long lectureId);

    Optional<CourseVideoEntity> findById(Long courseVideoId);

    CourseVideoEntity getByCourseIdAndIndex(Long courseId, int videoIndex);

    Integer countByCourseId(Long courseId);

    Integer countCompletedByCourseId(Long courseId);

    void deleteByCourseId(Long courseId);

    Optional<CourseVideoEntity> findByCourseIdAndPrevIndex(Long courseId, int videoIndex);
    
    VideoInfo getLastInfoByCourseId(Long courseId);

    List<VideoWatchInfo> getWatchInfoListByCourseIdAndSection(Long courseId, int section);

    List<VideoInfoForSchedule> getInfoForScheduleByCourseId(Long courseId);

    void updateSummaryId(Long videoId, long summaryId);
}
