package com.m9d.sroom.repository.course;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.global.model.Course;
import com.m9d.sroom.lecture.dto.response.CourseBrief;

import java.util.Date;
import java.util.List;

public interface CourseRepository {

    Long save(Course course);

    Course getById(Long courseId);

    void deleteById(Long courseId);

    Integer countByMemberId(Long memberId);

    Integer countCompletedByMemberId(Long memberId);

    void updateScheduleById(Long courseId, int weeks, Date expectedEndDate);

    void updateDurationById(Long courseId, int duration);

    void updateProgressById(Long courseId, int progress);



}
