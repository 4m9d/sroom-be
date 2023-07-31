package com.m9d.sroom.course.controller;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.service.CourseService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
@Slf4j
public class CourseController {

    private final JwtUtil jwtUtil;
    private final CourseService courseService;

    @Auth
    @GetMapping("")
    @Tag(name = "내 강의실 코스 리스트 불러오기")
    @Operation(summary = "내 강의코스 불러오기", description = "멤버ID를 입력받아 멤버의 등록 코스 리스트와 관련 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 코스 리스트를 불러왔습니다.", content = @Content(schema = @Schema(implementation = MyCourses.class)))
    public MyCourses getMyCourses() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        MyCourses myCourses = courseService.getMyCourses(memberId);

        return myCourses;
    }

    @Auth
    @PostMapping("")
    @Tag(name = "강의 등록")
    @Operation(summary = "강의 신규 등록", description = "강의코드를 입력받아 코스를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 코스를 등록하였습니다.", content = @Content(schema = @Schema(implementation = EnrolledCourseInfo.class)))
    public EnrolledCourseInfo enrollCourse(@Valid @RequestBody NewLecture newLecture, @RequestParam("use_schedule") boolean useSchedule) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        EnrolledCourseInfo enrolledCourseInfo = courseService.enrollCourse(memberId, newLecture, useSchedule);
        return enrolledCourseInfo;
    }

    @Auth
    @PostMapping("/{courseId}")
    public EnrolledCourseInfo addLectureInCourse(@PathVariable("courseId") Long courseId, @Valid @RequestBody NewLecture newLecture){
        Long memberId = jwtUtil.getMemberIdFromRequest();
        EnrolledCourseInfo enrolledCourseInfo = courseService.addLectureInCourse(memberId, courseId, newLecture);
        return enrolledCourseInfo;
    }
}
