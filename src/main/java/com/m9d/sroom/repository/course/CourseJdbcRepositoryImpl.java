package com.m9d.sroom.repository.course;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.global.model.Course;
import com.m9d.sroom.lecture.dto.response.CourseBrief;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class CourseJdbcRepositoryImpl implements CourseRepository{
    @Override
    public Long save(Course course) {
        return null;
    }

    @Override
    public Course getById(Long courseId) {
        return null;
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

    @Override
    public List<CourseInfo> getCourseInfoByMemberId(Long memberId) {
        return null;
    }

    @Override
    public List<CourseInfo> getLatestCourseInfoListByMemberId(Long memberId) {
        return null;
    }

    @Override
    public List<CourseBrief> getCourseBriefByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Long getMemberIdById(Long courseId) {
        return null;
    }

    @Override
    public void updateScheduleById(Long courseId, int weeks, Date expectedEndDate) {

    }

    @Override
    public void updateDurationById(Long courseId, int duration) {

    }

    @Override
    public void updateProgressById(Long courseId, int progress) {

    }
}
