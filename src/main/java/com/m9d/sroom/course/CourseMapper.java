package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.embedded.Scheduling;
import com.m9d.sroom.course.constant.CourseConstant;
import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.search.dto.response.CourseBrief;

public class CourseMapper {

    public static CourseBrief getBriefByEntity(CourseEntity course) {
        return CourseBrief.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getCourseTitle())
                .totalVideoCount(course.getCourseVideos().size())
                .build();
    }
}
