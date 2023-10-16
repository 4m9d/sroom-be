package com.m9d.sroom.course;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.playlist.PlaylistWithItemListSaved;
import com.m9d.sroom.util.ValidateUtil;
import com.m9d.sroom.util.annotation.Auth;
import com.m9d.sroom.video.VideoSaved;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.youtube.YoutubeService;
import com.m9d.sroom.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseControllerV2 {

    private final JwtUtil jwtUtil;
    private final CourseServiceV2 courseService;
    private final PlaylistService playlistService;
    private final VideoService videoService;

    @Auth
    @PostMapping("")
    public EnrolledCourseInfo enroll(@Valid @RequestBody NewLecture newLecture, @RequestParam("use_schedule") boolean useSchedule) {
        if (ValidateUtil.checkIfPlaylist(newLecture.getLectureCode())) {
            playlistService.putPlaylistWithItemListSaved(playlistService.getPlaylistWithItemList(newLecture.getLectureCode()));
            return courseService.enroll(jwtUtil.getMemberIdFromRequest(), newLecture, useSchedule, playlistService.getplaylistWithItemListSaved(newLecture.getLectureCode()));
        } else {
            videoService.putVideoSaved(videoService.getVideo(newLecture.getLectureCode()));
            return courseService.enroll(jwtUtil.getMemberIdFromRequest(), newLecture, useSchedule, videoService.getVideoSaved(newLecture.getLectureCode()));
        }
    }
}
