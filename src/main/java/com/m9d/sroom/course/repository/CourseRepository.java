package com.m9d.sroom.course.repository;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.sql.CourseSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        return null;
    }

    public HashSet<String> getChannelListByCourseId(Long courseId) {
        return new HashSet<>(jdbcTemplate.query(CourseSqlQuery.GET_CHANNELS_BY_COURSE_ID_QUERY, (rs, rowNum) -> rs.getString("channel"), courseId));
    }

    public int getTotalLectureCountByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseSqlQuery.GET_TOTAL_LECTURE_COUNT_BY_COURSE_ID_QUERY, (rs, rowNum) -> rs.getInt("lecture_count"), courseId);
    }

    public int getCompletedLectureCountByCourseId(Long courseId) {
        return jdbcTemplate.queryForObject(CourseSqlQuery.GET_COMPLETED_LECTURE_COUNT_BY_COURSE_ID_QUERY, (rs, rowNum) -> rs.getInt("completed_lecture_count"), courseId);
    }

    public Long saveCourse(Long memberId, String courseTitle, Long courseDuration, String thumbnail) {
        return null; //return courseId
    }

    public Long saveCourseWithSchedule(Long memberId, String courseTitle, Long courseDuration, String thumbnail, int weeks, int dailyTargetTime) {
        return null; //return courseId
    }

    public Long saveVideo(String videoCode, Long duration, String channel, String thumbnail, Long viewCount, String description, String title, String language, boolean licence) {
        return null; //return videoId
    }

    public Long savePlaylist(String playlistCode, String channel, String thumbnail, String description) {
        return null; //return playlistId
    }

    public Long savePlaylistVideo(Long playlistId, Long videoId, int videoIndex) {
        return null; //return playlistVideoId
    }

    public Long saveLecture(Long memberId, Long courseId, Long sourceId, String channel, boolean isPlaylist, int lectureIndex) {
        return null; //return lectureId
    }

    public Long getCourseIdByLectureId(Long lectureId){
        return null;
    }

}
