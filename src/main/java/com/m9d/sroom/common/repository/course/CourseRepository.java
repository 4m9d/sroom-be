package com.m9d.sroom.common.repository.course;

import com.m9d.sroom.common.entity.jdbctemplate.CourseEntity;
import com.m9d.sroom.search.dto.response.CourseBrief;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    CourseEntity save(CourseEntity course);

    CourseEntity getById(Long courseId);

    Optional<CourseEntity> findById(Long courseId);

    CourseEntity updateById(Long courseId, CourseEntity course);

    void deleteById(Long courseId);

    List<CourseEntity> getLatestOrderByMemberId(Long memberId);

    List<CourseBrief> getBriefListByMemberId(Long memberId);
}
