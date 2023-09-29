package com.m9d.sroom.repository.course;

import com.m9d.sroom.global.mapper.Course;
import com.m9d.sroom.global.mapper.CourseVideo;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    Course save(Course course);

    Course getById(Long courseId);

    Course updateById(Long courseId, Course course);

    void deleteById(Long courseId);

    List<Course> getByMemberId(Long memberId);

    List<Course> getLatestOrderByMemberId(Long memberId);

    Integer countByMemberId(Long memberId);

    Integer countCompletedByMemberId(Long memberId);
}
