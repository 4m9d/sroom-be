package com.m9d.sroom.course.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.m9d.sroom.course.dto.request.NewCourse;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistItem;
import com.m9d.sroom.util.youtube.resource.Video;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.m9d.sroom.util.youtube.YoutubeConstant.*;

@Service
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

    public void requestToFastApi(String lectureCode, String defaultLanguage) {

    }

    @Transactional
    public EnrolledCourseInfo enrollCourse(Long memberId, NewCourse newCourse, boolean useSchedule) {
        Long courseId = saveCourse(memberId, newCourse, useSchedule);
        boolean isPlaylist = youtubeUtil.checkIfPlaylist(newCourse.getLectureCode());

        Long sourceId;
        if (isPlaylist) {
            sourceId = getPlaylistInfo(newCourse);
        } else {
            sourceId = getVideoInfo(newCourse);
        }
        Long lectureId = courseRepository.saveLecture(memberId, courseId, sourceId, newCourse.getChannel(), isPlaylist, newCourse.getIndexCount());

        return EnrolledCourseInfo.builder()
                .courseId(courseId)
                .lectureId(lectureId)
                .build();
    }

    private Long getVideoInfo(NewCourse newCourse) {
        Optional<Long> videoIdOptional = courseRepository.findVideo(newCourse.getLectureCode());
        Long sourceId = videoIdOptional.orElseGet(() -> youtubeUtil.safeGet(saveVideo(newCourse.getLectureCode())));
        return sourceId;
    }

    private Long getPlaylistInfo(NewCourse newCourse) {
        Long sourceId;
        Optional<Long> playlistIdOptional = courseRepository.findPlaylist(newCourse.getLectureCode());
        if (playlistIdOptional.isEmpty()) {
            sourceId = courseRepository.savePlaylist(newCourse.getLectureCode(), newCourse.getChannel(), newCourse.getThumbnail(), newCourse.getDescription());
            savePlaylistItem(newCourse.getLectureCode(), sourceId);
        } else {
            sourceId = playlistIdOptional.get();
        }
        return sourceId;
    }

    private Long saveCourse(Long memberId, NewCourse newCourse, boolean useSchedule) {
        Long courseId;
        Long lectureDuration = dateUtil.convertTimeToSeconds(newCourse.getDuration());

        if (useSchedule) {
            courseId = courseRepository.saveCourseWithSchedule(memberId, newCourse.getTitle(), lectureDuration, newCourse.getThumbnail(), newCourse.getScheduling().size(), newCourse.getDailyTargetTime());
        } else {
            courseId = courseRepository.saveCourse(memberId, newCourse.getTitle(), lectureDuration, newCourse.getThumbnail());
        }
        return courseId;
    }

    private void savePlaylistItem(String lectureCode, Long playlistId) {
        String nextPageToken = null;
        do {
            nextPageToken = savePlaylistItemPerPage(lectureCode, playlistId, nextPageToken);
        } while (nextPageToken != null);
    }

    private String savePlaylistItemPerPage(String lectureCode, Long playlistId, String nextPageToken) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(PlaylistItem.builder()
                .playlistCode(lectureCode)
                .limit(DEFAULT_INDEX_COUNT)
                .nextPageToken(nextPageToken)
                .build());
        JsonNode playlistItem = youtubeUtil.safeGet(youtubeResource);

        for (JsonNode item : playlistItem.get(JSONNODE_ITEMS)) {
            int index = item.get(JSONNODE_SNIPPET).get(JSONNODE_POSITION).asInt();
            CompletableFuture<Long> videoIdFuture = saveVideo(item.get(JSONNODE_SNIPPET).get(JSONNODE_RESOURCE_ID).get(JSONNODE_VIDEO_ID).asText());
            videoIdFuture.thenAccept(videoId -> courseRepository.savePlaylistVideo(playlistId, videoId, index));
        }

        try {
            return playlistItem.get(JSONNODE_NEXT_PAGE_TOKEN).asText();
        } catch (NullPointerException e) {
            return null;
        }
    }


    @Async
    public CompletableFuture<Long> saveVideo(String videoCode) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(Video.builder()
                .videoCode(videoCode)
                .build());
        JsonNode videoNode = youtubeUtil.safeGet(youtubeResource).get(JSONNODE_ITEMS).get(FIRST_INDEX);

        String language = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_LANGUAGE).asText();
        String licence = videoNode.get(JSONNODE_STATUS).get(JSONNODE_LICENCE).asText();
        Long videoDuration = dateUtil.convertISOToSeconds(videoNode.get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText());
        String channel = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_CHANNEL_TITLE).asText();
        String description = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_DESCRIPTION).asText();
        String title = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_TITLE).asText();
        String thumbnail = youtubeUtil.selectThumbnail(videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));

        Long sourceId = courseRepository.saveVideo(videoCode, videoDuration, channel, thumbnail, description, title, language, licence);
        return CompletableFuture.completedFuture(sourceId);
    }
}
