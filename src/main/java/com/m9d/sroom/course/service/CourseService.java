package com.m9d.sroom.course.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.domain.Video;
import com.m9d.sroom.course.dto.request.NewCourse;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.util.youtube.YoutubeConstant.*;

@Service
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final YoutubeUtil youtubeUtil;
    private final DateUtil dateUtil;

    public CourseService(CourseRepository courseRepository, YoutubeUtil youtubeUtil, DateUtil dateUtil) {
        this.courseRepository = courseRepository;
        this.youtubeUtil = youtubeUtil;
        this.dateUtil = dateUtil;
    }

    public List<CourseInfo> getCourseList(Long memberId) {
        return null;
    }

    public void requestToFastApi(String videoCode, String defaultLanguage) {
        log.info("request to AI server. videoCode = {}, language = {}", videoCode, defaultLanguage);
    }

    @Transactional
    public EnrolledCourseInfo enrollCourse(Long memberId, NewCourse newCourse, boolean useSchedule) {
        Long courseId = saveCourse(memberId, newCourse, useSchedule);
        boolean isPlaylist = youtubeUtil.checkIfPlaylist(newCourse.getLectureCode());

        Long sourceId;
        if (isPlaylist) {
            sourceId = getPlaylistInfo(memberId, courseId, newCourse, useSchedule);
        } else {
            Video video = getVideoInfo(newCourse);
            sourceId = video.getVideoId();
            courseRepository.saveCourseVideo(memberId, courseId, sourceId, ENROLL_DEFAULT_SECTION, ENROLL_VIDEO_INDEX, ENROLL_LECTURE_INDEX);
        }
        Long lectureId = courseRepository.saveLecture(memberId, courseId, sourceId, newCourse.getChannel(), isPlaylist, newCourse.getIndexCount());

        log.info("course inserted. member = {}, lecture = {}", memberId, newCourse.getTitle());

        return EnrolledCourseInfo.builder()
                .courseId(courseId)
                .lectureId(lectureId)
                .build();
    }

    @Transactional
    public EnrolledCourseInfo addLectureInCourse(Long memberId, Long courseId, NewCourse newCourse) {

    }

    private Video getVideoInfo(NewCourse newCourse) {
        Optional<Video> videoIdOptional = courseRepository.findVideo(newCourse.getLectureCode());
        Video video = videoIdOptional.orElseGet(() -> youtubeUtil.safeGet(saveVideo(newCourse.getLectureCode())));
        return video;
    }

    private Long getPlaylistInfo(Long memberId, Long courseId, NewCourse newCourse, boolean useSchedule) {
        Long playlistId;
        Optional<Playlist> playlistIdOptional = courseRepository.findPlaylist(newCourse.getLectureCode());
        if (playlistIdOptional.isEmpty()) {
            playlistId = courseRepository.savePlaylist(newCourse.getLectureCode(), newCourse.getChannel(), newCourse.getThumbnail(), newCourse.getDescription());
            savePlaylistItem(newCourse.getLectureCode(), playlistId);
            saveCourseDuration(courseId, playlistId);
        } else {
            playlistId = playlistIdOptional.get().getPlaylistId();
            courseRepository.saveCourseDuration(courseId, playlistIdOptional.get().getDuration());
        }

        int videoCount = 1;
        int section = 0;
        int week = 0;

        List<Video> videoData = courseRepository.getVideoIdAndIndex(playlistId);
        for (Video videoInfo : videoData) {
            if (useSchedule) {
                if (videoCount > newCourse.getScheduling().get(week)) {
                    week++;
                    section++;
                    videoCount = 1;
                }
                courseRepository.saveCourseVideo(memberId, courseId, videoInfo.getVideoId(), section + 1, videoInfo.getIndex(), ENROLL_LECTURE_INDEX);
                videoCount++;
            } else {
                courseRepository.saveCourseVideo(memberId, courseId, videoInfo.getVideoId(), ENROLL_DEFAULT_SECTION, videoInfo.getIndex(), ENROLL_LECTURE_INDEX);
            }
        }

        return playlistId;
    }

    private void saveCourseDuration(Long courseId, Long playlistId) {
        int duration = courseRepository.getDurationByPlaylistId(playlistId);
        courseRepository.saveCourseDuration(courseId, duration);
    }

    private Long saveCourse(Long memberId, NewCourse newCourse, boolean useSchedule) {
        Long courseId;
        Long lectureDuration = dateUtil.convertTimeToSeconds(newCourse.getDuration());

        if (useSchedule) {
            validateScheduleField(newCourse);
            courseId = courseRepository.saveCourseWithSchedule(memberId, newCourse.getTitle(), lectureDuration, newCourse.getThumbnail(), newCourse.getScheduling().size(), newCourse.getDailyTargetTime());
        } else {
            courseId = courseRepository.saveCourse(memberId, newCourse.getTitle(), lectureDuration, newCourse.getThumbnail());
        }
        return courseId;
    }

    private void validateScheduleField(NewCourse newCourse) {
        if (newCourse.getDailyTargetTime() == 0 ||
                newCourse.getScheduling() == null ||
                newCourse.getExpectedEndTime() == null ||
                newCourse.getIndexCount() == 0) {
            throw new InvalidParameterException("스케줄 필드를 적절히 입력해주세요");
        }
    }

    private void savePlaylistItem(String playlistCode, Long playlistId) {
        String nextPageToken = null;
        do {
            nextPageToken = savePlaylistItemPerPage(playlistCode, playlistId, nextPageToken);
        } while (nextPageToken != null);
    }

    private String savePlaylistItemPerPage(String lectureCode, Long playlistId, String nextPageToken) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(PlaylistItemReq.builder()
                .playlistCode(lectureCode)
                .limit(DEFAULT_INDEX_COUNT)
                .nextPageToken(nextPageToken)
                .build());
        JsonNode playlistItem = youtubeUtil.safeGet(youtubeResource);

        for (JsonNode item : playlistItem.get(JSONNODE_ITEMS)) {
            int index = item.get(JSONNODE_SNIPPET).get(JSONNODE_POSITION).asInt();
            CompletableFuture<Video> videoFuture = saveVideo(item.get(JSONNODE_SNIPPET).get(JSONNODE_RESOURCE_ID).get(JSONNODE_VIDEO_ID).asText());
            videoFuture.thenAccept(video -> courseRepository.savePlaylistVideo(playlistId, video.getVideoId(), index + 1));
        }

        try {
            return playlistItem.get(JSONNODE_NEXT_PAGE_TOKEN).asText();
        } catch (NullPointerException e) {
            return null;
        }
    }


    @Async
    public CompletableFuture<Video> saveVideo(String videoCode) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(VideoReq.builder()
                .videoCode(videoCode)
                .build());
        JsonNode videoNode = youtubeUtil.safeGet(youtubeResource).get(JSONNODE_ITEMS).get(FIRST_INDEX);

        String language = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_LANGUAGE).asText();
        String licence = videoNode.get(JSONNODE_STATUS).get(JSONNODE_LICENCE).asText();
        int videoDuration = dateUtil.convertISOToSeconds(videoNode.get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText());
        String channel = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_CHANNEL_TITLE).asText();
        String description = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_DESCRIPTION).asText();
        String title = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_TITLE).asText();
        String thumbnail = youtubeUtil.selectThumbnail(videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));

        Long sourceId = courseRepository.saveVideo(videoCode, videoDuration, channel, thumbnail, description, title, language, licence);

        requestToFastApi(videoCode, language);
        return CompletableFuture.completedFuture(Video.builder()
                .videoId(sourceId)
                .duration(videoDuration)
                .build());
    }
}
