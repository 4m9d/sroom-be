package com.m9d.sroom.course;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.search.dto.request.LectureTimeRecord;
import com.m9d.sroom.search.dto.response.LectureStatus;
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
@RequestMapping("")
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final CourseServiceHelper courseServiceHelper;
    private final PlaylistService playlistService;
    private final VideoService videoService;
    private final JwtUtil jwtUtil;

    @Auth
    @GetMapping("/courses")
    @Tag(name = "내 강의실")
    @Operation(summary = "내 강의코스 불러오기", description = "멤버ID를 입력받아 멤버의 등록 코스 리스트와 관련 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 코스 리스트를 불러왔습니다.", content = @Content(schema = @Schema(implementation = MyCourses.class)))
    public MyCourses getMyCourses() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        MyCourses myCourses = courseService.getMyCourses(memberId);

        return myCourses;
    }

    @Auth
    @PostMapping("/courses")
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
    @PostMapping("/courses/{courseId}")
    @Tag(name = "강의 등록")
    @Operation(summary = "기존 코스 강의 등록", description = "강의코드와 코스ID를 받아 코스에 추가합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 코스에 강의를 추가하였습니다.", content = @Content(schema = @Schema(implementation = EnrolledCourseInfo.class)))
    public EnrolledCourseInfo addLectureInCourse(@PathVariable("courseId") Long courseId, @Valid @RequestBody NewLecture newLecture) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        courseServiceHelper.validateCourseForMember(memberId, courseId);

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
    @GetMapping("/courses/{courseId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "수강페이지 코스정보", description = "코스 ID를 받아 해당 코스 정보와 수강할 영상 리스트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 수강 정보를 반환하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseDetail.class))})
    public CourseDetail getCourseDetail(@PathVariable(name = "courseId") Long courseId) {
        courseServiceHelper.validateCourseForMember(jwtUtil.getMemberIdFromRequest(), courseId);
        return courseService.getCourseDetail(courseId);
    }

    @Auth
    @PutMapping("/lectures/{courseVideoId}/time")
    @Tag(name = "강의 수강")
    @Operation(summary = "시청중인 강의 학습시간 저장하기", description = "duration을 입력받아 업데이트하고, 70%가 넘었다면 수강완료 처리한다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 학습시간을 저장 하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LectureStatus.class))})
    public LectureStatus updateLectureTime(@PathVariable(name = "courseVideoId") Long courseVideoId, @Valid @RequestBody LectureTimeRecord record, @RequestParam(name = "isCompletedManually", required = false, defaultValue = "false") boolean isCompletedManually) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return courseService.updateLectureTime(memberId, courseVideoId, record.getViewDuration(), isCompletedManually);
    }

    @Auth
    @DeleteMapping("courses/{courseId}")
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
