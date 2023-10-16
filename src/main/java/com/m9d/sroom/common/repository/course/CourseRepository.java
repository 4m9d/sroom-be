package com.m9d.sroom.common.repository.course;

import com.m9d.sroom.common.dto.Course;
import com.m9d.sroom.lecture.dto.response.CourseBrief;

import java.util.List;

public interface CourseRepository {

    Course save(Course course);

    Course getById(Long courseId);

    Course updateById(Long courseId, Course course);

    void deleteById(Long courseId);

    List<Course> getLatestOrderByMemberId(Long memberId);

    List<CourseBrief> getBriefListByMemberId(Long memberId);
}
