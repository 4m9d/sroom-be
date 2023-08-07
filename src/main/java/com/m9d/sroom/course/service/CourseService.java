package com.m9d.sroom.course.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.m9d.sroom.course.domain.Course;
import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.domain.Video;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.util.DateUtil.DAYS_IN_WEEK;
import static com.m9d.sroom.util.DateUtil.SECONDS_IN_MINUTE;
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

    public MyCourses getMyCourses(Long memberId) {

        List<CourseInfo> courseInfoList = courseRepository.getCourseListByMemberId(memberId);
        int unfinishedCourseCount = getUnfinishedCourseCount(courseInfoList);

        int courseCount = courseInfoList.size();

        int completionRate = (int) ((float) (courseCount - unfinishedCourseCount) / courseCount * 100);

        for (int i = 0; i < courseInfoList.size(); i++) {

            Long courseId = courseInfoList.get(i).getCourseId();
            HashSet<String> channels = courseRepository.getChannelSetByCourseId(courseId);
            int lectureCount = courseRepository.getTotalLectureCountByCourseId(courseId);
            int completedLectureCount = courseRepository.getCompletedLectureCountByCourseId(courseId);

            courseInfoList.get(i).setChannels(String.join(", ", channels));
            courseInfoList.get(i).setTotalVideoCount(lectureCount);
            courseInfoList.get(i).setCompletedVideoCount(completedLectureCount);
        }

        MyCourses myCourses = MyCourses.builder()
                .unfinishedCourse(unfinishedCourseCount)
                .completionRate(completionRate)
                .courses(courseInfoList)
                .build();

        return myCourses;
    }

    public int getUnfinishedCourseCount(List<CourseInfo> courseInfoList) {

        int unfinishedCourseCount = 0;

        for (int i = 0; i < courseInfoList.size(); i++) {
            if (courseInfoList.get(i).getProgress() < 100) {
                unfinishedCourseCount++;
            }
        }

        return unfinishedCourseCount;
    }

    public void requestToFastApi(String videoCode, String defaultLanguage) {
        log.info("request to AI server. videoCode = {}, language = {}", videoCode, defaultLanguage);
    }

    @Transactional
    public EnrolledCourseInfo enrollCourse(Long memberId, NewLecture newLecture, boolean useSchedule) {
        EnrolledCourseInfo enrolledCourseInfo;

        boolean isPlaylist = youtubeUtil.checkIfPlaylist(newLecture.getLectureCode());
        if (isPlaylist) {
            enrolledCourseInfo = saveCourseWithPlaylist(memberId, newLecture, useSchedule);
        } else {
            enrolledCourseInfo = saveCourseWithVideo(memberId, newLecture, useSchedule);
        }

        log.info("course inserted. member = {}, lectureId = {}, title = {}", memberId, newLecture.getLectureCode(), enrolledCourseInfo.getTitle());
        return enrolledCourseInfo;
    }

    @Transactional
    public EnrolledCourseInfo addLectureInCourse(Long memberId, Long courseId, NewLecture newLecture) {
        validateCourseId(memberId, courseId);
        boolean isPlaylist = youtubeUtil.checkIfPlaylist(newLecture.getLectureCode());

        Course course = courseRepository.getCourse(courseId);

        EnrolledCourseInfo enrolledCourseInfo;

        if (isPlaylist) {
            Playlist playlist = putAndGetPlaylist(newLecture.getLectureCode());
            enrolledCourseInfo = addPlaylistInCourse(course, playlist);
        } else {
            Video video = youtubeUtil.safeGet(putAndGetVideo(newLecture.getLectureCode()));
            enrolledCourseInfo = addVideoInCourse(course, video);
        }

        return enrolledCourseInfo;
    }

    private EnrolledCourseInfo addVideoInCourse(Course course, Video video) {
        int lastLectureIndex = courseRepository.getLastLectureIndex(course.getCourseId());

        Long lectureId = courseRepository.saveLecture(course.getMemberId(), course.getCourseId(), video.getVideoId(), video.getChannel(), false, lastLectureIndex + 1);

        saveCourseVideo(course, video, lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }

        return EnrolledCourseInfo.builder()
                .title(video.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lectureId)
                .build();
    }

    private EnrolledCourseInfo addPlaylistInCourse(Course course, Playlist playlist) {
        int lastLectureIndex = courseRepository.getLastLectureIndex(course.getCourseId());

        Long lectureId = courseRepository.saveLecture(course.getMemberId(), course.getCourseId(), playlist.getPlaylistId(), playlist.getChannel(), true, lastLectureIndex + 1);

        saveCourseVideos(course, playlist, lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }
        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lectureId)
                .build();
    }

    private void scheduleVideos(Course course) {
        Date startDate = course.getStartDate();
        int dailyTargetTimeForSecond = course.getDailyTargetTime() * SECONDS_IN_MINUTE;
        int weeklyTargetTimeForSecond = dailyTargetTimeForSecond * DAYS_IN_WEEK;
        int section = 1;
        int currentSectionTime = 0;


        List<Video> videoList = courseRepository.getVideosByCourseId(course.getCourseId());
        int lastSectionTime = 0;

        for (Video video : videoList) {
            if (currentSectionTime + video.getDuration() > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += video.getDuration();
            lastSectionTime = currentSectionTime;

            courseRepository.updateVideoSection(course.getCourseId(), video.getVideoId(), section);
        }

        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTimeForSecond);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);
        Date expectedEndDate = calendar.getTime();

        courseRepository.updateSchedule(course.getCourseId(), section, expectedEndDate);
    }

    private void saveCourseVideos(Course course, Playlist playlist, int lectureIndex) {
        List<Video> enrolledVideoList = courseRepository.getVideoListByCourseId(course.getCourseId());
        int lastVideoIndex = enrolledVideoList.get(enrolledVideoList.size() - 1).getIndex();

        List<Video> videoList = courseRepository.getVideoIdAndIndex(playlist.getPlaylistId());
        int videoIndex = lastVideoIndex + 1;

        for (Video video : videoList) {
            courseRepository.saveCourseVideo(course.getMemberId(), course.getCourseId(), video.getVideoId(), ENROLL_DEFAULT_SECTION, videoIndex, lectureIndex);
            videoIndex++;
        }
    }

    private void saveCourseVideo(Course course, Video video, int lectureIndex) {
        List<Video> enrolledVideoList = courseRepository.getVideoListByCourseId(course.getCourseId());
        int lastVideoIndex = enrolledVideoList.get(enrolledVideoList.size() - 1).getIndex();

        courseRepository.saveCourseVideo(course.getMemberId(), course.getCourseId(), video.getVideoId(), ENROLL_DEFAULT_SECTION, lastVideoIndex + 1, lectureIndex);
    }

    private EnrolledCourseInfo saveCourseWithVideo(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Video video = youtubeUtil.safeGet(putAndGetVideo(newLecture.getLectureCode()));

        Long courseId;

        if (useSchedule) {
            validateScheduleField(newLecture);
            Date expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndTime());
            courseId = courseRepository.saveCourseWithSchedule(memberId, video.getTitle(), video.getDuration(), video.getThumbnail(), newLecture.getScheduling().size(), newLecture.getDailyTargetTime(), expectedEndDate);
            courseRepository.saveCourseVideo(memberId, courseId, video.getVideoId(), ENROLL_DEFAULT_SECTION_SCHEDULE, ENROLL_VIDEO_INDEX, ENROLL_LECTURE_INDEX);
        } else {
            courseId = courseRepository.saveCourse(memberId, video.getTitle(), video.getDuration(), video.getThumbnail());
            courseRepository.saveCourseVideo(memberId, courseId, video.getVideoId(), ENROLL_DEFAULT_SECTION, ENROLL_VIDEO_INDEX, ENROLL_LECTURE_INDEX);
        }

        Long lectureId = courseRepository.saveLecture(memberId, courseId, video.getVideoId(), video.getChannel(), false, ENROLL_LECTURE_INDEX);

        return EnrolledCourseInfo.builder()
                .title(video.getTitle())
                .courseId(courseId)
                .lectureId(lectureId)
                .build();
    }

    private EnrolledCourseInfo saveCourseWithPlaylist(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Playlist playlist = putAndGetPlaylist(newLecture.getLectureCode());

        Long courseId;
        if (useSchedule) {
            Date expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndTime());
            courseId = courseRepository.saveCourseWithSchedule(memberId, playlist.getTitle(), playlist.getDuration(), playlist.getThumbnail(), newLecture.getScheduling().size(), newLecture.getDailyTargetTime(), expectedEndDate);

        } else {
            courseId = courseRepository.saveCourse(memberId, playlist.getTitle(), playlist.getDuration(), playlist.getThumbnail());
        }

        saveCourseVideoListFirst(memberId, courseId, newLecture, playlist.getPlaylistId(), useSchedule);

        Long lectureId = courseRepository.saveLecture(memberId, courseId, playlist.getPlaylistId(), playlist.getChannel(), true, ENROLL_LECTURE_INDEX);

        return EnrolledCourseInfo.builder()
                .title(playlist.getTitle())
                .courseId(courseId)
                .lectureId(lectureId)
                .build();
    }

    private void saveCourseVideoListFirst(Long memberId, Long courseId, NewLecture newLecture, Long playlistId, boolean useSchedule) {
        int videoCount = 1;
        int section = 0;
        int week = 0;

        List<Video> videoData = courseRepository.getVideoIdAndIndex(playlistId);
        for (Video videoInfo : videoData) {
            if (useSchedule) {
                if (videoCount > newLecture.getScheduling().get(week)) {
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
    }

    private void validateCourseId(Long memberId, Long courseId) {
        Long actualMemberId = courseRepository.getMemberIdByCourseId(courseId);
        if (!memberId.equals(actualMemberId)) {
            throw new CourseNotMatchException();
        }
    }

    private Playlist putAndGetPlaylist(String playlistCode) {
        Optional<Playlist> playlistOptional = courseRepository.findPlaylist(playlistCode);
        Playlist playlist;
        Long playlistId;

        if (playlistOptional.isPresent() && dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            playlist = playlistOptional.get();
        } else {
            playlist = getPlaylistFromYoutube(playlistCode);
            if (playlistOptional.isEmpty()) {
                playlistId = courseRepository.savePlaylist(playlist);
            } else {
                playlistId = courseRepository.updatePlaylistAndGetId(playlist);
            }
            playlist.setPlaylistId(playlistId);
            courseRepository.deletePlaylistVideo(playlistId);
            putPlaylistItem(playlistCode, playlistId);
            int playlistDuration = courseRepository.getDurationByPlaylistId(playlistId);
            courseRepository.updatePlaylistDuration(playlistId, playlistDuration);
            playlist.setDuration(playlistDuration);
        }

        return playlist;
    }

    private void putPlaylistItem(String playlistCode, Long playlistId) {
        String nextPageToken = null;
        do {
            nextPageToken = putPlaylistItemPerPage(playlistCode, playlistId, nextPageToken);
        } while (nextPageToken != null);

    }

    private Playlist getPlaylistFromYoutube(String playlistCode) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(PlaylistReq.builder()
                .playlistCode(playlistCode)
                .build());
        JsonNode playlistNode = youtubeUtil.safeGet(youtubeResource);

        youtubeUtil.validateNodeIfNotFound(playlistNode);

        JsonNode snippetJsonNode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET);
        String thumbnail = youtubeUtil.selectThumbnail(snippetJsonNode.get(JSONNODE_THUMBNAILS));
        String title = snippetJsonNode.get(JSONNODE_TITLE).asText();
        String channel = snippetJsonNode.get(JSONNODE_CHANNEL_TITLE).asText();
        String description = snippetJsonNode.get(JSONNODE_DESCRIPTION).asText();
        String publishedAtString = snippetJsonNode.get(JSONNODE_PUBLISHED_AT).asText();
        Timestamp publishedAt = Timestamp.from(Instant.parse(publishedAtString));
        int videoCount = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_ITEM_COUNT).asInt();

        return Playlist.builder()
                .playlistCode(playlistCode)
                .title(title)
                .thumbnail(thumbnail)
                .channel(channel)
                .description(description)
                .publishedAt(publishedAt)
                .lectureCount(videoCount)
                .build();
    }

    private void validateScheduleField(NewLecture newLecture) {
        if (newLecture.getDailyTargetTime() == 0 ||
                newLecture.getScheduling().equals(null) ||
                newLecture.getExpectedEndTime() == null) {
            throw new InvalidParameterException("스케줄 필드를 적절히 입력해주세요");
        }
    }

    private String putPlaylistItemPerPage(String lectureCode, Long playlistId, String nextPageToken) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(PlaylistItemReq.builder()
                .playlistCode(lectureCode)
                .limit(DEFAULT_INDEX_COUNT)
                .nextPageToken(nextPageToken)
                .build());
        JsonNode playlistItem = youtubeUtil.safeGet(youtubeResource);

        for (JsonNode item : playlistItem.get(JSONNODE_ITEMS)) {
            if(item.get(JSONNODE_STATUS).get(JSONNODE_PRIVACY_STATUS).asText().equals(JSONNODE_PRIVATE)){
                continue;
            }
            int index = item.get(JSONNODE_SNIPPET).get(JSONNODE_POSITION).asInt();
            CompletableFuture<Video> videoFuture = putAndGetVideo(item.get(JSONNODE_SNIPPET).get(JSONNODE_RESOURCE_ID).get(JSONNODE_VIDEO_ID).asText());
            videoFuture.thenAccept(video -> courseRepository.savePlaylistVideo(playlistId, video.getVideoId(), index + 1));
        }

        try {
            return playlistItem.get(JSONNODE_NEXT_PAGE_TOKEN).asText();
        } catch (NullPointerException e) {
            return null;
        }
    }


    @Async
    public CompletableFuture<Video> putAndGetVideo(String videoCode) {
        Optional<Video> videoOptional = courseRepository.findVideo(videoCode);

        Video video;
        Long videoId;
        if (videoOptional.isPresent() && dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            video = videoOptional.get();
            videoId = video.getVideoId();
            video.setVideoId(videoId);
        } else {
            video = getVideoFromYoutube(videoCode);
            videoId = courseRepository.saveVideo(video);
            video.setVideoId(videoId);
        }

        return CompletableFuture.completedFuture(video);
    }

    private Video getVideoFromYoutube(String videoCode) {
        CompletableFuture<JsonNode> youtubeResource = youtubeUtil.getYoutubeResource(VideoReq.builder()
                .videoCode(videoCode)
                .build());
        JsonNode videoNode = youtubeUtil.safeGet(youtubeResource).get(JSONNODE_ITEMS).get(FIRST_INDEX);

        youtubeUtil.validateNodeIfNotFound(youtubeUtil.safeGet(youtubeResource));

        String language;
        if (videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_LANGUAGE) != null) {
            language = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_LANGUAGE).asText();
        } else {
            language = UNKNOWN_LANGUAGE;
        }
        String licence = videoNode.get(JSONNODE_STATUS).get(JSONNODE_LICENCE).asText();
        int videoDuration = dateUtil.convertISOToSeconds(videoNode.get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText());
        String channel = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_CHANNEL_TITLE).asText();
        String description = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_DESCRIPTION).asText();
        String title = videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_TITLE).asText();
        String thumbnail = youtubeUtil.selectThumbnail(videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));
        long viewCount = videoNode.get(JSONNODE_STATISTICS).get(JSONNODE_VIEW_COUNT).asLong();
        Timestamp publishedAt = dateUtil.convertISOToTimestamp(videoNode.get(JSONNODE_SNIPPET).get(JSONNODE_PUBLISHED_AT).asText());

        return Video.builder()
                .videoCode(videoCode)
                .channel(channel)
                .thumbnail(thumbnail)
                .language(language)
                .license(licence)
                .duration(videoDuration)
                .description(description)
                .publishedAt(publishedAt)
                .viewCount(viewCount)
                .title(title)
                .build();
    }
}
