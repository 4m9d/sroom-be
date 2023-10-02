package com.m9d.sroom.repository.coursevideo;

import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.global.mapper.CourseVideo;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.VideoWatchInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseVideoJdbcRepositoryImpl implements CourseVideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseVideoJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CourseVideo save(CourseVideo courseVideo) {
        jdbcTemplate.update(CourseVideoRepositorySql.SAVE,
                courseVideo.getCourseId(),
                courseVideo.getVideoId(),
                courseVideo.getSection(),
                courseVideo.getVideoIndex(),
                courseVideo.getSummaryId(),
                courseVideo.getLectureIndex(),
                courseVideo.getMemberId(),
                courseVideo.getLastViewTime(),
                courseVideo.getMaxDuration(),
                courseVideo.getLectureId());
        return getById(jdbcTemplate.queryForObject(CourseVideoRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public CourseVideo updateById(Long courseVideoId, CourseVideo courseVideo) {
        jdbcTemplate.update(CourseVideoRepositorySql.UPDATE_BY_ID,
                courseVideo.getCourseId(),
                courseVideo.getVideoId(),
                courseVideo.getSection(),
                courseVideo.getVideoIndex(),
                courseVideo.getStartTime(),
                courseVideo.isComplete(),
                courseVideo.getSummaryId(),
                courseVideo.getLectureIndex(),
                courseVideo.getMemberId(),
                courseVideo.getLastViewTime(),
                courseVideo.getMaxDuration(),
                courseVideo.getLectureId(),
                courseVideoId);
        return getById(courseVideoId);
    }

    @Override
    public CourseVideo getById(Long courseVideoId) {
        return jdbcTemplate.queryForObject(CourseVideoRepositorySql.GET_BY_ID, CourseVideo.getRowMapper(), courseVideoId);
    }

    @Override
    public List<CourseVideo> getListByCourseId(Long courseId) {
        return jdbcTemplate.query(CourseVideoRepositorySql.GET_LIST_BY_COURSE_ID, CourseVideo.getRowMapper(), courseId);
    }

    @Override
    public List<CourseVideo> getListByLectureId(Long lectureId) {
        return jdbcTemplate.query(CourseVideoRepositorySql.GET_LIST_BY_LECTURE_ID,
                CourseVideo.getRowMapper(), lectureId);
    }

    @Override
    public Optional<CourseVideo> findById(Long courseVideoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseVideoRepositorySql.GET_BY_ID, CourseVideo.getRowMapper(), courseVideoId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Integer countByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseVideoRepositorySql.COOUNT_BY_COURSE_ID,
                (rs, rowNum) -> rs.getInt("count"), courseId);
    }

    @Override
    public Integer countCompletedByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseVideoRepositorySql.COMPLETED_COUNT_BY_COURSE_ID,
                (rs, rowNum) -> rs.getInt("completed_count"), courseId);
    }

    @Override
    @Transactional
    public void deleteByCourseId(Long courseId) {
        jdbcTemplate.update(CourseVideoRepositorySql.DELETE_BY_COURSE_ID, courseId);
    }

    @Override
    public Optional<CourseVideo> findByCourseIdAndPrevIndex(Long courseId, int videoIndex) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseVideoRepositorySql.GET_BY_COURSE_ID_AND_PREV_INDEX,
                    CourseVideo.getRowMapper(), courseId, videoIndex));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public LastVideoInfo getLastByCourseId(Long courseId) {
        return null;
    }

    @Override
    public LastVideoInfo getLastInfoByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseVideoRepositorySql.GET_LAST_INFO_BY_COURSE_ID, (rs, rowNum) -> LastVideoInfo.builder()
                        .videoId(rs.getLong("video_id"))
                        .videoTitle(rs.getString("title"))
                        .videoCode(rs.getString("video_code"))
                        .channel(rs.getString("channel"))
                        .lastViewDuration(rs.getInt("start_time"))
                        .courseVideoId(rs.getLong("course_video_id"))
                        .build(),
                courseId);
    }

    @Override
    public List<VideoWatchInfo> getWatchInfoListByCourseIdAndSection(Long courseId, int section) {
        return jdbcTemplate.query(CourseVideoRepositorySql.GET_WATCH_INFO_LIST_BY_COURSE_ID_AND_SECTION, (rs, rowNum) -> VideoWatchInfo.builder()
                        .videoId(rs.getLong("video_id"))
                        .videoCode(rs.getString("video_code"))
                        .channel(rs.getString("channel"))
                        .videoTitle(rs.getString("title"))
                        .videoIndex(rs.getInt("video_index"))
                        .completed(rs.getBoolean("is_complete"))
                        .lastViewDuration(rs.getInt("start_time"))
                        .videoDuration(rs.getInt("duration"))
                        .courseVideoId(rs.getLong("course_video_id"))
                        .build(),
                courseId, section);
    }

    @Override
    public List<VideoInfoForSchedule> getInfoForScheduleByCourseId(Long courseId) {
        return jdbcTemplate.query(CourseVideoRepositorySql.GET_INFO_FOR_SCHEDULE_BY_COURSE_ID, (rs, rowNum) -> VideoInfoForSchedule.builder()
                        .courseVideo(CourseVideo.getRowMapper().mapRow(rs, rowNum))
                        .duration(rs.getInt("duration"))
                        .build()
                , courseId);
    }

    @Override
    public void updateSummaryId(Long videoId, long summaryId) {
        jdbcTemplate.update(CourseVideoRepositorySql.UPDATE_SUMMARY_ID, summaryId, videoId);
    }
}
