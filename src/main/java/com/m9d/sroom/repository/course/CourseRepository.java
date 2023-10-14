package com.m9d.sroom.repository.course;

import com.m9d.sroom.global.mapper.CourseDto;
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
