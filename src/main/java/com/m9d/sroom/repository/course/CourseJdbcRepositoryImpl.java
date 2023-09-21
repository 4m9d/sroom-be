package com.m9d.sroom.repository.course;

import com.m9d.sroom.global.mapper.Course;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class CourseJdbcRepositoryImpl implements CourseRepository{
    @Override
    public Course save(Course course) {
        return null;
    }

    @Override
    public Course getById(Long courseId) {
        return null;
    }

    @Override
    public void updateById(Long courseId, Course course) {

    }

    @Override
    public void deleteById(Long courseId) {

    }

    @Override
    public Integer countByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Integer countCompletedByMemberId(Long memberId) {
        return null;
    }
}
