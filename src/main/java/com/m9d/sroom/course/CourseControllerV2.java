package com.m9d.sroom.course;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.ValidateUtil;
import com.m9d.sroom.util.annotation.Auth;
import com.m9d.sroom.video.VideoService;
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
public class CourseControllerV2 {

    private final CourseServiceV2 courseService;
    private final PlaylistService playlistService;
    private final VideoService videoService;
    private final JwtUtil jwtUtil;

    @Auth
    @PostMapping("")
    @Tag(name = "강의 등록")
    @Operation(summary = "강의 신규 등록", description = "강의코드를 입력받아 코스를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 코스를 등록하였습니다.", content = @Content(schema = @Schema(implementation = EnrolledCourseInfo.class)))
    public EnrolledCourseInfo enroll(@Valid @RequestBody NewLecture newLecture,
                                     @RequestParam("use_schedule") boolean useSchedule) {
        if (ValidateUtil.checkIfPlaylist(newLecture.getLectureCode())) {
            playlistService.putPlaylistWithItemList(
                    playlistService.getRecentPlaylistWithItemList(newLecture.getLectureCode())
            );
            return courseService.enroll(jwtUtil.getMemberIdFromRequest(), newLecture,
                    useSchedule, playlistService.getEnrollContentInfo(newLecture.getLectureCode()));
        } else {
            videoService.putVideo(videoService.getRecentVideo(newLecture.getLectureCode()));
            return courseService.enroll(jwtUtil.getMemberIdFromRequest(), newLecture,
                    useSchedule, videoService.getEnrollContentInfo(newLecture.getLectureCode()));
        }
    }

    @Auth
    @PostMapping("/{courseId}")
    @Tag(name = "강의 등록")
    @Operation(summary = "기존 코스 강의 등록", description = "강의코드와 코스ID를 받아 코스에 추가합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 코스에 강의를 추가하였습니다.", content = @Content(schema = @Schema(implementation = EnrolledCourseInfo.class)))
    public EnrolledCourseInfo addLectureInCourse(@PathVariable("courseId") Long courseId, @Valid @RequestBody NewLecture newLecture) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        if (!courseService.validateCourseForMember(memberId, courseId)) {
            throw new CourseNotMatchException();
        }

        if (ValidateUtil.checkIfPlaylist(newLecture.getLectureCode())) {
            playlistService.putPlaylistWithItemList(
                    playlistService.getRecentPlaylistWithItemList(newLecture.getLectureCode())
            );
            return courseService.addLecture(memberId, courseId, playlistService.getEnrollContentInfo(newLecture.getLectureCode()));
        } else {
            videoService.putVideo(
                    videoService.getRecentVideo(newLecture.getLectureCode())
            );
            return courseService.addLecture(memberId, courseId, videoService.getEnrollContentInfo(newLecture.getLectureCode()));
        }
    }

    @Auth
    @GetMapping("/{courseId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "수강페이지 코스정보", description = "코스 ID를 받아 해당 코스 정보와 수강할 영상 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 수강 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetail.class))})
    public CourseDetail getCourseDetail(@PathVariable(name = "courseId") Long courseId) {
        if (!courseService.validateCourseForMember(jwtUtil.getMemberIdFromRequest(), courseId)) {
            throw new CourseNotMatchException();
        }
        return courseService.getCourseDetail(courseId);
    }
}
