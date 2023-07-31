package com.m9d.sroom.course.controller;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.service.CourseService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
@Slf4j
public class CourseController {

    private final JwtUtil jwtUtil;
    private final CourseService courseService;

    @Auth
    @GetMapping("")
    public MyCourses getCourses() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        MyCourses myCourses = courseService.getMyCourses(memberId);

        return myCourses;
    }

    @Auth
    @PostMapping("")
    public EnrolledCourseInfo enrollCourse() {

        EnrolledCourseInfo enrolledCourseInfo = courseService.enrollCourse(null, null);
        return null;
    }
}
