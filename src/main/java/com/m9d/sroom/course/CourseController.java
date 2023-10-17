package com.m9d.sroom.course;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import com.m9d.sroom.youtube.YoutubeService;
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
    @Tag(name = "내 강의실")
    @Operation(summary = "내 강의코스 불러오기", description = "멤버ID를 입력받아 멤버의 등록 코스 리스트와 관련 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 코스 리스트를 불러왔습니다.", content = @Content(schema = @Schema(implementation = MyCourses.class)))
    public MyCourses getMyCourses() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        MyCourses myCourses = courseService.getMyCourses(memberId);

        return myCourses;
    }

//    @Auth
//    @PostMapping("")
//    @Tag(name = "강의 등록")
//    @Operation(summary = "강의 신규 등록", description = "강의코드를 입력받아 코스를 생성합니다.")
//    @ApiResponse(responseCode = "200", description = "성공적으로 강의 코스를 등록하였습니다.", content = @Content(schema = @Schema(implementation = EnrolledCourseInfo.class)))
//    public EnrolledCourseInfo enrollCourse(@Valid @RequestBody NewLecture newLecture, @RequestParam("use_schedule") boolean useSchedule) {
//        if (YoutubeService.checkIfPlaylist(newLecture.getLectureCode())) {
//            return courseService.saveCourseWithPlaylist(jwtUtil.getMemberIdFromRequest(), newLecture, useSchedule,
//                    courseService.getPlaylistWithUpdate(newLecture.getLectureCode()));
//        } else {
//            return courseService.saveCourseWithVideo(jwtUtil.getMemberIdFromRequest(), newLecture, useSchedule);
//        }
//    }
//
//    @Auth
//    @PostMapping("/{courseId}")
//    @Tag(name = "강의 등록")
//    @Operation(summary = "기존 코스 강의 등록", description = "강의코드와 코스ID를 받아 코스에 추가합니다.")
//    @ApiResponse(responseCode = "200", description = "성공적으로 코스에 강의를 추가하였습니다.", content = @Content(schema = @Schema(implementation = EnrolledCourseInfo.class)))
//    public EnrolledCourseInfo addLectureInCourse(@PathVariable("courseId") Long courseId, @Valid @RequestBody NewLecture newLecture){
//        if (!courseService.validateCourseId(jwtUtil.getMemberIdFromRequest(), courseId)) {
//            throw new CourseNotMatchException();
//        }
//        if (YoutubeService.checkIfPlaylist(newLecture.getLectureCode())) {
//            return courseService.addPlaylistInCourse(courseId, courseService.getPlaylistWithUpdate(newLecture.getLectureCode()));
//        } else {
//            return courseService.addVideoInCourse(courseId, courseService.getVideoAndRequestToAiServer(newLecture.getLectureCode()));
//        }
//    }

    @Auth
    @GetMapping("/{courseId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "수강페이지 코스정보", description = "코스 ID를 받아 해당 코스 정보와 수강할 영상 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 수강 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetail.class))})
    public CourseDetail getCourseDetail(@PathVariable(name = "courseId") Long courseId) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return courseService.getCourseDetail(memberId, courseId);
    }

    @Auth
    @DeleteMapping("/{courseId}")
    @Tag(name = "내 강의실")
    @Operation(summary = "강의 코스 삭제", description = "지정한 강의 코스를 삭제하고, 업데이트된 강의 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "강의코스 삭제가 성공했으며, 정상적으로 결과를 반환했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MyCourses.class))})
    public MyCourses deleteCourse(@PathVariable(name = "courseId") Long courseId) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        courseService.deleteCourse(memberId, courseId);
        MyCourses myCourses = courseService.getMyCourses(memberId);
        return myCourses;
    }
}
