package com.m9d.sroom.repository.course;

import com.m9d.sroom.global.mapper.Course;
import com.m9d.sroom.lecture.dto.response.VideoInfoForCreateSection;

import java.util.List;

public interface CourseRepository {

    Long save(Course course);

    Course getById(Long courseId);

    void updateById(Long courseId, Course course);

    void deleteById(Long courseId);

    Integer countByMemberId(Long memberId);

    Integer countCompletedByMemberId(Long memberId);

    List<VideoInfoForCreateSection> getVideoInfoForCreateSectionByCourseIdAndSection(Long courseId, int section);
}
