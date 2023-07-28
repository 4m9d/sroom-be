package com.m9d.sroom.course.repository;

import com.m9d.sroom.course.dto.response.CourseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CourseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CourseInfo> getCourseListByMemberId(int memberId) {
        return null;
    }

    public List<String> getChannelListByCourseId(int courseId) {
        return null;
    }

    public Long saveCourse(Long memberId, String courseTitle, Long courseDuration, String thumbnail) {
        String query = "INSERT INTO COURSE (member_id, course_title, course_duration, thumbnail) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(query, memberId, courseTitle, courseDuration, thumbnail);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long saveCourseWithSchedule(Long memberId, String courseTitle, Long courseDuration, String thumbnail, int weeks, int dailyTargetTime) {
        String query = "INSERT INTO COURSE (member_id, course_title, course_duration, thumbnail, weeks, daily_target_time) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, memberId, courseTitle, courseDuration, thumbnail, weeks, dailyTargetTime);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long saveVideo(String videoCode, Long duration, String channel, String thumbnail, String description, String title, String language, String licence) {
        String query = "INSERT INTO VIDEO (video_code, duration, channel, thumbnail, description, title, language, license) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, videoCode, duration, channel, thumbnail, description, title, language, licence);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long savePlaylist(String playlistCode, String channel, String thumbnail, String description) {
        String query = "INSERT INTO PLAYLIST (playlist_code, channel, thumbnail, description) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(query, playlistCode, channel, thumbnail, description);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long savePlaylistVideo(Long playlistId, Long videoId, int videoIndex) {
        String query = "INSERT INTO PLAYLISTVIDEO (playlist_id, video_id, video_index) VALUES (?, ?, ?)";

        jdbcTemplate.update(query, playlistId, videoId, videoIndex);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long saveLecture(Long memberId, Long courseId, Long sourceId, String channel, boolean isPlaylist, int lectureIndex) {
        String query = "INSERT INTO LECTURE (member_id, course_id, source_id, channel, is_playlist, lecture_index) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, memberId, courseId, sourceId, channel, isPlaylist ? 1 : 0, lectureIndex);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long getCourseIdByLectureId(Long lectureId) {
        String query = "SELECT course_id FROM LECTURE WHERE lecture_id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, lectureId);
    }

    public Optional<Long> findPlaylist(String lectureCode) {
        String query = "SELECT playlist_id FROM PLAYLIST WHERE playlist_code = ?";
        Long playlistId = queryForObjectOrNull(query, (rs, rowNum) -> rs.getLong(1), lectureCode);
        return Optional.ofNullable(playlistId);
    }

    public Optional<Long> findVideo(String lectureCode) {
        String query = "SELECT video_id FROM VIDEO WHERE video_code = ?";
        Long videoId = queryForObjectOrNull(query, (rs, rowNum) -> rs.getLong(1), lectureCode);
        return Optional.ofNullable(videoId);
    }

    private <T> T queryForObjectOrNull(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
