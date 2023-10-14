package com.m9d.sroom.course.repository;

import com.m9d.sroom.course.CourseDto;
import com.m9d.sroom.lecture.dto.response.CourseBrief;

import java.util.List;

public interface CourseRepository {

    CourseDto save(CourseDto courseDto);

    CourseDto getById(Long courseId);

    CourseDto updateById(Long courseId, CourseDto courseDto);

    void deleteById(Long courseId);

    List<CourseDto> getLatestOrderByMemberId(Long memberId);

    List<CourseBrief> getBriefListByMemberId(Long memberId);
}
