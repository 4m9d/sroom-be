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

    public List<String> getChannelListByCourseId(int courseId) { return null; }
}
