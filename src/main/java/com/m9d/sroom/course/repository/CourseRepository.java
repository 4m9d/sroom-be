package com.m9d.sroom.course.repository;

import com.m9d.sroom.global.model.*;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.exception.CourseNotFoundException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.sql.CourseSqlQuery;
import com.m9d.sroom.lecture.dto.response.CourseBrief;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.VideoBrief;
import com.m9d.sroom.material.model.CourseAndVideoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static com.m9d.sroom.course.sql.CourseSqlQuery.*;
import static com.m9d.sroom.lecture.sql.LectureSqlQuery.*;

import java.util.HashSet;
import java.util.List;

@Repository
public class CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CourseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CourseInfo> getCourseListByMemberId(Long memberId) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return jdbcTemplate.query(CourseSqlQuery.GET_COURSES_BY_MEMBER_ID_QUERY,
                (rs, rowNum) -> CourseInfo.builder()
                        .courseId(rs.getLong("course_id"))
                        .duration(rs.getInt("course_duration"))
                        .thumbnail(rs.getString("thumbnail"))
                        .progress(rs.getInt("progress"))
                        .courseTitle(rs.getString("course_title"))
                        .lastViewTime(dateFormat.format(rs.getTimestamp("last_view_time")))
                        .build(),
                memberId);
    }

    public HashSet<String> getChannelSetByCourseId(Long courseId) {
        return new HashSet<>(jdbcTemplate.query(CourseSqlQuery.GET_CHANNELS_BY_COURSE_ID_QUERY, (rs, rowNum) -> rs.getString("channel"), courseId));
    }

    public int getTotalLectureCountByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseSqlQuery.GET_TOTAL_LECTURE_COUNT_BY_COURSE_ID_QUERY, (rs, rowNum) -> rs.getInt("lecture_count"), courseId);
    }

    public int getCompletedVideoCountByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseSqlQuery.GET_COMPLETED_LECTURE_COUNT_BY_COURSE_ID_QUERY, (rs, rowNum) -> rs.getInt("completed_lecture_count"), courseId);
    }

    public Long saveCourse(Long memberId, String courseTitle, int courseDuration, String thumbnail) {
        jdbcTemplate.update(SAVE_COURSE_QUERY, memberId, courseTitle, courseDuration, thumbnail);

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public Long saveCourseWithSchedule(Long memberId, String courseTitle, int courseDuration, String thumbnail, int weeks, int dailyTargetTime, Date expectedEndDate) {
        jdbcTemplate.update(SAVE_COURSE_WITH_SCHEDULE_QUERY, memberId, courseTitle, courseDuration, thumbnail, weeks, dailyTargetTime, 1, expectedEndDate);

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public Long saveVideo(Video video) {
        jdbcTemplate.update(SAVE_VIDEO_QUERY, video.getVideoCode(), video.getDuration(), video.getChannel(), video.getThumbnail(), video.getDescription(), video.getTitle(), video.getLanguage(), video.getLicense(), video.getViewCount(), video.getPublishedAt());

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public Long savePlaylist(Playlist playlist) {
        jdbcTemplate.update(SAVE_PLAYLIST_QUERY, playlist.getPlaylistCode(), playlist.getTitle(), playlist.getChannel(), playlist.getThumbnail(), playlist.getDescription(), playlist.getPublishedAt(), playlist.getLectureCount());

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public void savePlaylistVideo(Long playlistId, Long videoId, int videoIndex) {
        jdbcTemplate.update(SAVE_PLAYLIST_VIDEO_QUERY, playlistId, videoId, videoIndex);
    }

    public Long saveLecture(Long memberId, Long courseId, Long sourceId, String channel, boolean isPlaylist, int lectureIndex) {
        jdbcTemplate.update(SAVE_LECTURE_QUERY, memberId, courseId, sourceId, channel, isPlaylist ? 1 : 0, lectureIndex);

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public void saveCourseVideo(Long memberId, Long courseId, Long videoId, int section, int videoIndex, int lectureIndex) {
        jdbcTemplate.update(SAVE_COURSE_VIDEO_QUERY, memberId, courseId, videoId, section, videoIndex, lectureIndex);
    }

    public Long getCourseIdByLectureId(Long lectureId) {
        return jdbcTemplate.queryForObject(GET_COURSE_ID_BY_LECTURE_ID_QUERY, Long.class, lectureId);
    }

    public List<CourseBrief> getCourseBriefListByMember(Long memberId) {
        return jdbcTemplate.query(GET_COURSE_LIST_QUERY,
                ((rs, rowNum) -> CourseBrief.builder()
                        .courseTitle(rs.getString("course_title"))
                        .courseId(rs.getLong("course_id"))
                        .totalVideoCount(rs.getInt("video_count"))
                        .build()), memberId
        );
    }

    public Optional<Playlist> findPlaylist(String lectureCode) {
        try {
            Playlist playlist = queryForObjectOrNull(FIND_PLAYLIST_QUERY, (rs, rowNum) -> Playlist.builder()
                    .playlistId(rs.getLong("playlist_id"))
                    .title(rs.getString("title"))
                    .channel(rs.getString("channel"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .thumbnail(rs.getString("thumbnail"))
                    .updatedAt(rs.getTimestamp("updated_at"))
                    .build(), lectureCode);
            return Optional.ofNullable(playlist);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Video> findVideo(String lectureCode) {
        try {
            Video video = queryForObjectOrNull(FIND_VIDEO_QUERY, (rs, rowNum) -> Video.builder()
                    .videoId(rs.getLong("video_id"))
                    .videoCode(rs.getString("video_code"))
                    .channel(rs.getString("channel"))
                    .thumbnail(rs.getString("thumbnail"))
                    .language(rs.getString("language"))
                    .license(rs.getString("license"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .viewCount(rs.getLong("view_count"))
                    .title(rs.getString("title"))
                    .updatedAt(rs.getTimestamp("updated_at"))
                    .publishedAt(rs.getTimestamp("published_at"))
                    .build(), lectureCode);
            return Optional.ofNullable(video);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private <T> T queryForObjectOrNull(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Video> getVideoInfoFromPlaylistVideo(Long playlistId) {
        return jdbcTemplate.query(GET_VIDEO_ID_AND_INDEX_QUERY, ((rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .build()), playlistId);
    }

    public List<Integer> getVideoDurationsByPlaylistId(Long playlistId) {
        return jdbcTemplate.query(GET_DURATION_BY_PLAYLIST_ID_QUERY, (rs, rowNum) -> rs.getInt("duration"), playlistId);
    }

    public Long getMemberIdByCourseId(Long courseId) {
        try {
            return jdbcTemplate.queryForObject(GET_MEMBER_ID_BY_COURSE_ID_QUERY, Long.class, courseId);
        } catch (EmptyResultDataAccessException e) {
            throw new CourseNotFoundException();
        }
    }

    public Long updatePlaylistAndGetId(Playlist playlist) {
        jdbcTemplate.update(UPDATE_PLAYLIST_AND_GET_ID_QUERY, playlist.getTitle(), playlist.getChannel(), playlist.getDescription(), playlist.getPublishedAt(), playlist.getLectureCount(), playlist.getPlaylistCode());

        return jdbcTemplate.queryForObject(GET_PLAYLIST_ID_BY_PLAYLIST_CODE, (rs, rowNum) -> rs.getLong("playlist_id"), playlist.getPlaylistCode());
    }

    public void deletePlaylistVideo(Long playlistId) {
        jdbcTemplate.update(DELETE_PLAYLIST_VIDEO_QUERY, playlistId);
    }

    public void updatePlaylistDuration(Long playlistId, int playlistDuration) {
        jdbcTemplate.update(UPDATE_PLAYLIST_DURATION_QUERY, playlistDuration, playlistId);
    }

    public Course getCourse(Long courseId) {
        return jdbcTemplate.queryForObject(GET_COURSE_QUERY, ((rs, rowNum) -> Course.builder()
                .courseId(courseId)
                .memberId(rs.getLong("member_id"))
                .title(rs.getString("course_title"))
                .duration(rs.getInt("course_duration"))
                .scheduled(rs.getBoolean("is_scheduled"))
                .thumbnail(rs.getString("thumbnail"))
                .weeks(rs.getInt("weeks"))
                .startDate(rs.getDate("start_date"))
                .expectedEndTime(rs.getTimestamp("expected_end_date"))
                .dailyTargetTime(rs.getInt("daily_target_time"))
                .build()), courseId);
    }

    public List<Video> getVideoListByCourseId(Long courseId) {
        return jdbcTemplate.query(GET_VIDEO_LIST_BY_COURSE_ID_QUERY, ((rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .complete(rs.getBoolean("is_complete"))
                .build()), courseId);
    }

    public List<Integer> getLectureIndexList(Long courseId) {
        return jdbcTemplate.query(GET_LECTURE_INDEX_LIST_QUERY, (rs, rowNum) -> rs.getInt("lecture_index"), courseId);
    }

    public List<Video> getVideosByCourseId(Long courseId) {
        return jdbcTemplate.query(GET_VIDEOS_BY_COURSE_ID_QUERY, (rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .duration(rs.getInt("duration"))
                .build(), courseId);
    }

    public void updateVideoSection(Long courseId, int index, int section) {
        jdbcTemplate.update(UPDATE_VIDEO_SECTION_QUERY, section, courseId, index);
    }

    public void updateSchedule(Long courseId, int weeks, Date expectedEndDate) {
        jdbcTemplate.update(UPDATE_SCHEDULE_QUERY, weeks, expectedEndDate, courseId);
    }

    public void updateVideo(Video video) {
        jdbcTemplate.update(UPDATE_VIDEO_QUERY, video.getDuration(), video.getChannel(), video.getThumbnail(), video.getLanguage(), video.getLicense(), video.getDescription(), video.getViewCount(), video.getTitle(), video.getVideoCode());
    }

    public void updateCourseDuration(Long courseId, int duration) {
        jdbcTemplate.update(UPDATE_COURSE_DURATION_QUERY, duration, courseId);
    }

    public LastVideoInfo getLastCourseVideo(Long courseId) {
        try {
            return jdbcTemplate.queryForObject(GET_LAST_COURSE_VIDEO, (rs, rowNum) -> LastVideoInfo.builder()
                    .videoId(rs.getLong("video_id"))
                    .videoTitle(rs.getString("title"))
                    .videoCode(rs.getString("video_code"))
                    .channel(rs.getString("channel"))
                    .lastViewDuration(rs.getInt("start_time"))
                    .courseVideoId(rs.getLong("course_video_id"))
                    .build(), courseId);
        } catch (EmptyResultDataAccessException e) {
            throw new CourseVideoNotFoundException();
        }
    }

    public List<VideoBrief> getVideoBrief(Long courseId, int section) {
        return jdbcTemplate.query(GET_VIDEO_BRIEF_QUERY, (rs, rowNum) -> VideoBrief.builder()
                .videoId(rs.getLong("video_id"))
                .videoCode(rs.getString("video_code"))
                .channel(rs.getString("channel"))
                .videoTitle(rs.getString("title"))
                .videoIndex(rs.getInt("video_index"))
                .completed(rs.getBoolean("is_complete"))
                .lastViewDuration(rs.getInt("start_time"))
                .videoDuration(rs.getInt("duration"))
                .courseVideoId(rs.getLong("course_video_id"))
                .build(), courseId, section);
    }

    public Long findCourseVideoId(Long courseId, Long videoId) {
        try {
            return jdbcTemplate.queryForObject(FIND_COURSE_VIDEO_ID_QUERY, Long.class, courseId, videoId);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public Long findCourseIdByCourseVideoId(Long courseVideoId) {
        try {
            return jdbcTemplate.queryForObject(FIND_COURSE_ID_BY_COURSE_VIDEO_ID, Long.class, courseVideoId);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public Optional<CourseVideo> findCourseVideoById(Long courseVideoId) {
        try {
            CourseVideo courseVideo = jdbcTemplate.queryForObject(FIND_COURSE_VIDEO_BY_ID, (rs, rowNum) -> CourseVideo.builder()
                    .courseVideoId(rs.getLong("course_video_id"))
                    .courseId(rs.getLong("course_id"))
                    .videoId(rs.getLong("video_id"))
                    .memberId(rs.getLong("member_id"))
                    .section(rs.getInt("section"))
                    .videoIndex(rs.getInt("video_index"))
                    .startTime(rs.getInt("start_time"))
                    .complete(rs.getBoolean("is_complete"))
                    .summaryId(rs.getLong("summary_id"))
                    .lectureIndex(rs.getInt("lecture_index"))
                    .lastViewTime(rs.getTimestamp("last_view_time"))
                    .maxDuration(rs.getInt("max_duration"))
                    .build(), courseVideoId);
            return Optional.ofNullable(courseVideo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateCourseProgress(Long courseId, int courseProgress) {
        jdbcTemplate.update(UPDATE_COURSE_PROGRESS_QUERY, courseProgress, courseId);
    }

    public Integer getVideoCountByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(GET_VIDEO_COUNT_BY_COURSE_ID, Integer.class, courseId);
    }

    public void updateCourseVideo(CourseVideo courseVideo) {
        jdbcTemplate.update(UPDATE_COURSE_VIDEO, courseVideo.getSection(), courseVideo.getVideoIndex(), courseVideo.getStartTime(), courseVideo.isComplete(), courseVideo.getSummaryId(), courseVideo.getLectureIndex(), Timestamp.valueOf(LocalDateTime.now()), courseVideo.getMaxDuration(), courseVideo.getCourseVideoId());
    }

    public Optional<CourseDailyLog> findCourseDailyLogByDate(Long courseId, java.sql.Date date) {
        try {
            CourseDailyLog dailyLog = jdbcTemplate.queryForObject(FIND_COURSE_DAILY_LOG_QUERY, (rs, rowNum) -> CourseDailyLog.builder()
                    .courseDailyLogId(rs.getLong("course_daily_log_id"))
                    .memberId(rs.getLong("member_id"))
                    .courseId(courseId)
                    .dailyLogDate(date)
                    .learningTime(rs.getInt("learning_time"))
                    .quizCount(rs.getInt("quiz_count"))
                    .lectureCount(rs.getInt("lecture_count"))
                    .build(), courseId, date);
            return Optional.ofNullable(dailyLog);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void saveCourseDailyLog(CourseDailyLog dailyLog) {
        jdbcTemplate.update(SAVE_COURSE_DAILY_LOG_QUERY, dailyLog.getMemberId(), dailyLog.getCourseId(), dailyLog.getDailyLogDate(), dailyLog.getLearningTime(), dailyLog.getQuizCount(), dailyLog.getLectureCount());
    }

    public void updateCourseDailyLog(CourseDailyLog dailyLog) {
        jdbcTemplate.update(UPDATE_COURSE_DAILY_LOG_QUERY, dailyLog.getLearningTime(), dailyLog.getQuizCount(), dailyLog.getLectureCount(), dailyLog.getCourseDailyLogId());
    }

    public void updateVideoViewStatus(CourseVideo courseVideo) {
        jdbcTemplate.update(UPDATE_VIDEO_VIEW_STATUS_QUERY, courseVideo.getMaxDuration(), courseVideo.getStartTime(), courseVideo.isComplete(), courseVideo.getLastViewTime(), courseVideo.getCourseVideoId());
    }

    public void updateCourseDailyLogQuizCount(Long courseId, java.sql.Date date, int quizCount) {
        jdbcTemplate.update(UPDATE_QUIZ_COUNT_LOG_QUERY, quizCount, courseId, date);
    }

    public CourseAndVideoId getCourseAndVideoId(Long courseVideoId) {
        try {
            return jdbcTemplate.queryForObject(GET_COURSE_AND_VIDEO_ID_QUERY, (rs, rowNum) -> CourseAndVideoId.builder()
                    .videoId(rs.getLong("video_id"))
                    .courseId(rs.getLong("course_id"))
                    .build(), courseVideoId);
        } catch (EmptyResultDataAccessException e) {
            throw new CourseVideoNotFoundException();
        }
    }

    public Integer findQuizCountByDailyLog(Long courseId, java.sql.Date date) {
        try {
            return jdbcTemplate.queryForObject(GET_QUIZ_COUNT_BY_DAILY_LOG_QUERY, Integer.class, courseId, date);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer getCourseCountByMemberId(Long memberId) {
        return jdbcTemplate.queryForObject(GET_COURSE_COUNT_BY_MEMBER_ID_QUERY, Integer.class, memberId);
    }

    public Integer getCompletedCourseCountByMemberId(Long memberId) {
        return jdbcTemplate.queryForObject(GET_COMPLETED_COURSE_COUNT_BY_MEMBER_Id_QUERY, Integer.class, memberId);
    }

    public Long getCourseVideoByPrevIndex(Long courseId, int videoIndex) {
        try {
            return jdbcTemplate.queryForObject(GET_COURSE_VIDEO_ID_BY_PREV_INDEX_QUERY, Long.class, courseId, videoIndex);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateLastViewTimeById(Long courseVideoId, Timestamp time) {
        jdbcTemplate.update(UPDATE_LAST_VIEW_TIME_BY_ID_QUERY, time, courseVideoId);
    }
}
