package com.m9d.sroom.course.repository;

import com.m9d.sroom.course.domain.Course;
import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.domain.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    public Long saveCourse(Long memberId, String courseTitle, int courseDuration, String thumbnail) {
        String query = "INSERT INTO COURSE (member_id, course_title, course_duration, thumbnail) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(query, memberId, courseTitle, courseDuration, thumbnail);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long saveCourseWithSchedule(Long memberId, String courseTitle, int courseDuration, String thumbnail, int weeks, int dailyTargetTime, Date expectedEndDate) {
        String query = "INSERT INTO COURSE (member_id, course_title, course_duration, thumbnail, weeks, daily_target_time, is_scheduled, expected_end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, memberId, courseTitle, courseDuration, thumbnail, weeks, dailyTargetTime, 1, expectedEndDate);

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long saveVideo(Video video) {
        String query = "INSERT INTO VIDEO (video_code, duration, channel, thumbnail, description, title, language, license) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, video.getVideoCode(), video.getDuration(), video.getChannel(), video.getThumbnail(), video.getDescription(), video.getTitle(), video.getLanguage(), video.getLicense());

        query = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    public Long savePlaylist(Playlist playlist) {
        String query = "INSERT INTO PLAYLIST (playlist_code, title, channel, thumbnail, description, published_at) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, playlist.getPlaylistCode(), playlist.getTitle(), playlist.getChannel(), playlist.getThumbnail(), playlist.getDescription(), playlist.getPublishedAt());

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

    public void saveCourseVideo(Long memberId, Long courseId, Long videoId, int section, int videoIndex, int lectureIndex) {
        String query = "INSERT INTO COURSEVIDEO (member_id, course_id, video_id, section, video_index, lecture_index) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(query, memberId, courseId, videoId, section, videoIndex, lectureIndex);
    }

    public Long getCourseIdByLectureId(Long lectureId) {
        String query = "SELECT course_id FROM LECTURE WHERE lecture_id = ?";
        return jdbcTemplate.queryForObject(query, Long.class, lectureId);
    }

    public Optional<Playlist> findPlaylist(String lectureCode) {
        String query = "SELECT playlist_id, title, channel, thumbnail, description, duration, updated_at FROM PLAYLIST WHERE playlist_code = ?";
        Playlist playlist = queryForObjectOrNull(query, (rs, rowNum) -> Playlist.builder()
                .playlistId(rs.getLong("playlist_id"))
                .title(rs.getString("title"))
                .channel(rs.getString("channel"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .thumbnail(rs.getString("thumbnail"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build(), lectureCode);
        return Optional.ofNullable(playlist);
    }

    public Optional<Video> findVideo(String lectureCode) {
        String query = "SELECT video_id, video_code, channel, thumbnail, language, license, duration, description, title, updated_at FROM VIDEO WHERE video_code = ?";
        Video video = queryForObjectOrNull(query, (rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .videoCode(rs.getString("video_code"))
                .channel(rs.getString("channel"))
                .thumbnail(rs.getString("thumbnail"))
                .language(rs.getString("language"))
                .license(rs.getString("license"))
                .duration(rs.getInt("duration"))
                .title(rs.getString("title"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build(), lectureCode);
        return Optional.ofNullable(video);
    }

    private <T> T queryForObjectOrNull(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Video> getVideoIdAndIndex(Long playlistId) {
        String query = "SELECT video_id, video_index FROM PLAYLISTVIDEO WHERE playlist_id = ? ORDER BY video_index";

        return jdbcTemplate.query(query, ((rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .build()), playlistId);
    }

    public void saveCourseDuration(Long courseId, int duration) {
        String query = "UPDATE COURSE SET duration = ? WHERE course_id = ?";

        jdbcTemplate.update(query, duration, courseId);
    }

    public int getDurationByPlaylistId(Long playlistId) {
        String query = "SELECT SUM(v.duration) FROM VIDEO v " +
                "INNER JOIN PLAYLISTVIDEO pv ON v.video_id = pv.video_id " +
                "WHERE pv.playlist_id = ?";

        Integer totalDuration = jdbcTemplate.queryForObject(query, Integer.class, playlistId);
        return totalDuration == null ? 0 : totalDuration;
    }

    public Long getMemberIdByCourseId(Long courseId) {
        String query = "SELECT member_id FROM COURSE WHERE course_id = ?";

        return jdbcTemplate.queryForObject(query, Long.class, courseId);
    }

    public List<Video> getVideoIdListByPlaylistId(Long playlistId) {
        String query = "SELECT video_id, video_index FROM PLAYLISTVIDEO WHERE playlsit_id = ?";

        return jdbcTemplate.query(query, ((rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .build()), playlistId);
    }

    public Long updatePlaylistAndGetId(Playlist playlist) {
        String updateQuery = "UPDATE PLAYLIST SET title = ?, channel = ?, description = ?, published_at = ? WHERE playlist_code = ?";

        jdbcTemplate.update(updateQuery, playlist.getTitle(), playlist.getChannel(), playlist.getDescription(), playlist.getPublishedAt(), playlist.getPlaylistCode());

        String selectQuery = "SELECT playlist_id FROM PLAYLIST WHERE playlist_code = ?";

        return jdbcTemplate.queryForObject(selectQuery, (rs, rowNum) -> rs.getLong("playlist_id"), playlist.getPlaylistCode());
    }

    public Long updateVideoAndGetId(Video video) {
        String updateQuery = "UPDATE VIDEO SET video_code = ?, channel = ?, thumbnail = ?, language = ?, license = ?, duration = ?, description = ?, title = ? WHERE video_code = ?";

        jdbcTemplate.update(updateQuery, video.getVideoCode(), video.getChannel(), video.getThumbnail(), video.getLanguage(), video.getLicense(), video.getDuration(), video.getDescription(), video.getTitle(), video.getVideoCode());

        String selectQuery = "SELECT video_id From VIDEO WHERE video_code = ?";

        return jdbcTemplate.queryForObject(selectQuery, (rs, rowNum) -> rs.getLong("video_id"), video.getVideoCode());
    }

    public void deletePlaylistVideo(Long playlistId) {
        String query = "DELETE FROM PLAYLISTVIDEO WHERE playlist_id = ?";

        jdbcTemplate.update(query, playlistId);
    }

    public void updatePlaylistDuration(Long playlistId, int playlistDuration) {
        String query = "UPDATE PLAYLIST SET duration = ? WHERE playlist_id = ?";

        jdbcTemplate.update(query, playlistDuration, playlistId);
    }

    public Course getCourse(Long courseId) {
        String query = "SELECT member_id, course_title, course_duration, is_scheduled, weeks, start_date, expected_end_date, daily_target_time FROM COURSE WHERE course_id = ?";

        return jdbcTemplate.queryForObject(query, ((rs, rowNum) -> Course.builder()
                .courseId(courseId)
                .memberId(rs.getLong("member_id"))
                .title(rs.getString("course_title"))
                .duration(rs.getInt("course_duration"))
                .scheduled(rs.getBoolean("is_scheduled"))
                .weeks(rs.getInt("weeks"))
                .startDate(rs.getDate("start_date"))
                .expectedEndTime(rs.getTimestamp("expected_end_date"))
                .dailyTargetTime(rs.getInt("daily_target_time"))
                .build()), courseId);
    }

    public int getVideoDuration(Long videoId) {
        String query = "SELECT duration FROM VIDEO WHERE video_id = ?";

        return jdbcTemplate.queryForObject(query, (rs, rowNum) -> rs.getInt("duration"), videoId);
    }

    public List<Video> getVideoListByCourseId(Long courseId) {
        String query = "SELECT video_id, video_index, is_complete FROM COURSEVIDEO WHERE course_id = ? ORDER BY video_index";

        return jdbcTemplate.query(query, ((rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .complete(rs.getBoolean("is_complete"))
                .build()), courseId);
    }

    public int getLastLectureIndex(Long courseId) {
        String query = "SELECT MAX(lecture_index) FROM LECTURE WHERE course_id = ?";

        try {
            Integer lastIndex = jdbcTemplate.queryForObject(query, new SingleColumnRowMapper<>(), courseId);
            return lastIndex != null ? lastIndex : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public List<Video> getVideosByCourseId(Long courseId) {
        String query = "SELECT v.video_id, cv.video_index, v.duration " +
                "FROM COURSEVIDEO cv INNER JOIN VIDEO v ON cv.video_id = v.video_id " +
                "WHERE cv.course_id = ? " +
                "ORDER BY cv.video_index ASC";

        return jdbcTemplate.query(query, (rs, rowNum) -> Video.builder()
                .videoId(rs.getLong("video_id"))
                .index(rs.getInt("video_index"))
                .duration(rs.getInt("duration"))
                .build(), courseId);
    }

    public void updateVideoSection(Long courseId, Long videoId, int section) {
        String query = "UPDATE COURSEVIDEO SET section = ? WHERE course_id = ? AND video_id = ?";

        jdbcTemplate.update(query, section, courseId, videoId);
    }

    public void updateSchedule(Long courseId, int weeks, Date expectedEndDate) {
        String sql = "UPDATE COURSE SET weeks = ?, expected_end_date = ? WHERE course_id = ?";

        jdbcTemplate.update(sql, weeks, expectedEndDate, courseId);
    }
}
