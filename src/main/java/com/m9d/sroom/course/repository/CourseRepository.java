package com.m9d.sroom.course.repository;

import com.m9d.sroom.course.dto.response.CourseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
