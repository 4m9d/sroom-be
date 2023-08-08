package com.m9d.sroom.course.service;

import com.m9d.sroom.course.domain.Course;
import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.domain.Video;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.model.PlaylistPageResult;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.Section;
import com.m9d.sroom.lecture.dto.response.VideoBrief;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeApi;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistSnippetVo;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.video.VideoItemVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.util.DateUtil.DAYS_IN_WEEK;
import static com.m9d.sroom.util.DateUtil.SECONDS_IN_MINUTE;
import static com.m9d.sroom.util.youtube.YoutubeUtil.*;

@Service
@Transactional
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final DateUtil dateUtil;
    private final YoutubeApi youtubeApi;
    private final YoutubeUtil youtubeUtil;

    public CourseService(CourseRepository courseRepository, DateUtil dateUtil, YoutubeApi youtubeApi, YoutubeUtil youtubeUtil) {
        this.courseRepository = courseRepository;
        this.dateUtil = dateUtil;
        this.youtubeApi = youtubeApi;
        this.youtubeUtil = youtubeUtil;
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

    public EnrolledCourseInfo addLectureInCourse(Long memberId, Long courseId, NewLecture newLecture) {
        validateCourseId(memberId, courseId);
        boolean isPlaylist = youtubeUtil.checkIfPlaylist(newLecture.getLectureCode());
        Course course = courseRepository.getCourse(courseId);

        EnrolledCourseInfo enrolledCourseInfo;
        if (isPlaylist) {
            Playlist playlist = getPlaylistWithUpdate(newLecture.getLectureCode());
            enrolledCourseInfo = addPlaylistInCourse(course, playlist);
        } else {
            Video video;
            video = safeGetVideo(newLecture.getLectureCode());
            enrolledCourseInfo = addVideoInCourse(course, video);
        }
        return enrolledCourseInfo;
    }

    private Video safeGetVideo(String videoCode) {
        Video video;
        try {
            video = getVideoWithUpdateAsync(videoCode).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return video;
    }

    private EnrolledCourseInfo addVideoInCourse(Course course, Video video) {
        List<Integer> lectureIndexList = courseRepository.getLectureIndexList(course.getCourseId());
        int lastLectureIndex = getMaxInList(lectureIndexList);

        Long lectureId = courseRepository.saveLecture(course.getMemberId(), course.getCourseId(), video.getVideoId(), video.getChannel(), false, lastLectureIndex + 1);

        saveCourseVideo(course, video, lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }
        courseRepository.updateCourseDuration(course.getCourseId(), course.getDuration() + video.getDuration());

        return EnrolledCourseInfo.builder()
                .title(video.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lectureId)
                .build();
    }

    private void saveCourseVideo(Course course, Video video, int lectureIndex) {
        List<Video> enrolledVideoList = courseRepository.getVideoListByCourseId(course.getCourseId());
        int lastVideoIndex = enrolledVideoList.get(enrolledVideoList.size() - 1).getIndex();

        courseRepository.saveCourseVideo(course.getMemberId(), course.getCourseId(), video.getVideoId(), ENROLL_DEFAULT_SECTION, lastVideoIndex + 1, lectureIndex);
    }

    private EnrolledCourseInfo addPlaylistInCourse(Course course, Playlist playlist) {
        List<Integer> lectureIndexList = courseRepository.getLectureIndexList(course.getCourseId());
        int lastLectureIndex = getMaxInList(lectureIndexList);

        Long lectureId = courseRepository.saveLecture(course.getMemberId(), course.getCourseId(), playlist.getPlaylistId(), playlist.getChannel(), true, lastLectureIndex + 1);

        saveCourseVideoList(course, playlist, lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }
        courseRepository.updateCourseDuration(course.getCourseId(), course.getDuration() + playlist.getDuration());
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
            if (currentSectionTime + (video.getDuration() / 2) > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += video.getDuration();
            lastSectionTime = currentSectionTime;

            courseRepository.updateVideoSection(course.getCourseId(), video.getIndex(), section);
        }

        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTimeForSecond);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);
        Date expectedEndDate = calendar.getTime();

        courseRepository.updateSchedule(course.getCourseId(), section, expectedEndDate);
    }

    private void saveCourseVideoList(Course course, Playlist playlist, int lectureIndex) {
        List<Video> enrolledVideoList = courseRepository.getVideoListByCourseId(course.getCourseId());
        int lastVideoIndex = enrolledVideoList.get(enrolledVideoList.size() - 1).getIndex();

        List<Video> videoList = courseRepository.getVideoIdAndIndex(playlist.getPlaylistId());
        int videoIndex = lastVideoIndex + 1;

        for (Video video : videoList) {
            courseRepository.saveCourseVideo(course.getMemberId(), course.getCourseId(), video.getVideoId(), ENROLL_DEFAULT_SECTION, videoIndex, lectureIndex);
            videoIndex++;
        }
    }

    public EnrolledCourseInfo saveCourseWithPlaylist(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Playlist playlist = getPlaylistWithUpdate(newLecture.getLectureCode());

        Long courseId;
        if (useSchedule) {
            validateScheduleField(newLecture);
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

    private Playlist getPlaylistWithUpdate(String playlistCode) {
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
            int playlistDuration = putPlaylistItemAndGetPlaylistDuration(playlistCode, playlistId, playlist.getLectureCount());
            courseRepository.updatePlaylistDuration(playlistId, playlistDuration);
            playlist.setDuration(playlistDuration);
        }

        return playlist;
    }

    private Playlist getPlaylistFromYoutube(String playlistCode) {
        Mono<PlaylistVo> playlistVoMono = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                .playlistCode(playlistCode)
                .build());
        PlaylistVo playlistVo = safeGetVo(playlistVoMono);

        PlaylistSnippetVo snippetVo = playlistVo.getItems().get(FIRST_INDEX).getSnippet();
        String thumbnail = youtubeUtil.selectThumbnailInVo(snippetVo.getThumbnails());

        return Playlist.builder()
                .playlistCode(playlistCode)
                .title(snippetVo.getTitle())
                .thumbnail(thumbnail)
                .channel(snippetVo.getChannelTitle())
                .description(snippetVo.getDescription())
                .publishedAt(dateUtil.convertISOToTimestamp(snippetVo.getPublishedAt()))
                .lectureCount(playlistVo.getItems().get(FIRST_INDEX).getContentDetails().getItemCount())
                .lectureCount(playlistVo.getItems().get(FIRST_INDEX).getContentDetails().getItemCount())
                .build();
    }

    public int putPlaylistItemAndGetPlaylistDuration(String playlistCode, Long playlistId, int lectureCount) {
        String nextPageToken = null;
        int playlistDuration = 0;
        int pageCount = (lectureCount / DEFAULT_INDEX_COUNT) + 1;
        for (int i = 0; i < pageCount; i++) {
            PlaylistPageResult result = putPlaylistItemPerPage(playlistCode, playlistId, nextPageToken);
            nextPageToken = result.getNextPageToken();
            playlistDuration += result.getTotalDurationPerPage();
        }
        return playlistDuration;
    }

    private PlaylistPageResult putPlaylistItemPerPage(String lectureCode, Long playlistId, String nextPageToken) {
        Mono<PlaylistVideoVo> playlistVideoVoMono = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
                .playlistCode(lectureCode)
                .limit(DEFAULT_INDEX_COUNT)
                .nextPageToken(nextPageToken)
                .build());
        PlaylistVideoVo playlistVideoVo = safeGetVo(playlistVideoVoMono);

        List<CompletableFuture<Video>> futureVideos = new ArrayList<>();
        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (itemVo.getStatus().getPrivacyStatus().equals(JSONNODE_PRIVATE)) {
                continue;
            }
            CompletableFuture<Video> videoFuture = getVideoWithUpdateAsync(itemVo.getSnippet().getResourceId().getVideoId());
            futureVideos.add(videoFuture);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureVideos.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Video>> allVideoFuture = allFutures.thenApply(v ->
                futureVideos.stream()
                        .map(future -> future.join())
                        .collect(Collectors.toList()));
        List<Video> videoList = allVideoFuture.join();
        int totalDurationPerPage = videoList.stream().mapToInt(Video::getDuration).sum();
        saveVideoList(playlistId, videoList, playlistVideoVo.getItems());

        try {
            nextPageToken = playlistVideoVo.getNextPageToken();
        } catch (NullPointerException e) {
            nextPageToken = null;
        }

        return new PlaylistPageResult(nextPageToken, totalDurationPerPage);
    }

    public void saveVideoList(Long playlistId, List<Video> videos, List<PlaylistVideoItemVo> items) {
        int playlistDurationPlus = 0;
        for (int i = 0; i < videos.size(); i++) {
            int index = items.get(i).getSnippet().getPosition();
            courseRepository.savePlaylistVideo(playlistId, videos.get(i).getVideoId(), index + 1);
        }
    }

    @Async
    public CompletableFuture<Video> getVideoWithUpdateAsync(String videoCode) {
        Optional<Video> videoOptional = courseRepository.findVideo(videoCode);
        CompletableFuture<Video> result;

        if (videoOptional.isPresent() && dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            result = CompletableFuture.completedFuture(videoOptional.get());
        } else {
            result = CompletableFuture.supplyAsync(() -> {
                Video video = getVideoFromYoutube(videoCode);
                Long videoId = videoOptional.isPresent() ? updateVideo(videoOptional.get()) : courseRepository.saveVideo(video);
                video.setVideoId(videoId);
                return video;
            });
        }
        return result;
    }

    private Long updateVideo(Video video) {
        courseRepository.updateVideo(video);
        return video.getVideoId();
    }

    private Video getVideoFromYoutube(String videoCode) {
        Mono<VideoVo> videoVoMono = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(videoCode)
                .build());
        VideoItemVo itemVo = safeGetVo(videoVoMono).getItems().get(FIRST_INDEX);

        String language;
        if (itemVo.getSnippet().getDefaultAudioLanguage() != null) {
            language = itemVo.getSnippet().getDefaultAudioLanguage();
        } else {
            language = UNKNOWN_LANGUAGE;
        }
        int videoDuration = dateUtil.convertISOToSeconds(itemVo.getContentDetails().getDuration());
        String thumbnail = youtubeUtil.selectThumbnailInVo(itemVo.getSnippet().getThumbnails());
        Timestamp publishedAt = dateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt());

        return Video.builder()
                .videoCode(videoCode)
                .channel(itemVo.getSnippet().getChannelTitle())
                .thumbnail(thumbnail)
                .language(language)
                .license(itemVo.getStatus().getLicense())
                .duration(videoDuration)
                .description(itemVo.getSnippet().getDescription())
                .publishedAt(publishedAt)
                .viewCount(itemVo.getStatistics().getViewCount())
                .title(itemVo.getSnippet().getTitle())
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

    private EnrolledCourseInfo saveCourseWithVideo(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Video video = safeGetVideo(newLecture.getLectureCode());

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

    public <T> T safeGetVo(Mono<T> vo) {
        if (vo == null) {
            log.warn("youtube data api 실행에 실패하였습니다.");
            throw new RuntimeException();
        } else {
            return vo.block();
        }
    }

    private void validateScheduleField(NewLecture newLecture) {
        if (newLecture.getDailyTargetTime() == 0 ||
                newLecture.getScheduling().equals(null) ||
                newLecture.getExpectedEndTime() == null) {
            throw new InvalidParameterException("스케줄 필드를 적절히 입력해주세요");
        }
    }

    private void validateCourseId(Long memberId, Long courseId) {
        Long actualMemberId = courseRepository.getMemberIdByCourseId(courseId);
        if (!memberId.equals(actualMemberId)) {
            throw new CourseNotMatchException();
        }
    }

    private int getMaxInList(List<Integer> lectureIndexList) {
        return Collections.max(lectureIndexList);
    }

    public CourseDetail getCourseDetail(Long memberId, Long courseId) {
        validateCourseId(memberId, courseId);
        Course course = courseRepository.getCourse(courseId);

        HashSet<String> channels = courseRepository.getChannelSetByCourseId(courseId);
        LastVideoInfo lastVideoInfo = courseRepository.getLastCourseVideo(courseId);
        List<Section> sections = getSectionList(course);
        int currentDuration = sections.stream()
                .mapToInt(Section::getCurrentWeekDuration)
                .sum();
        int videoCount = sections.stream()
                .mapToInt(section -> section.getVideos().size())
                .sum();
        int completedVideoCount = sections.stream()
                .mapToInt(section -> (int) section.getVideos().stream()
                        .filter(VideoBrief::isCompleted)
                        .count())
                .sum();
        int progress = (int) ((double) currentDuration / course.getDuration() * 100);

        return CourseDetail.builder()
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .useSchedule(course.isScheduled())
                .channels(String.join(", ", channels))
                .courseDuration(course.getDuration())
                .currentDuration(currentDuration)
                .videoCount(videoCount)
                .completedVideoCount(completedVideoCount)
                .progress(progress)
                .lastVideo(lastVideoInfo)
                .sections(sections)
                .build();
    }

    private List<Section> getSectionList(Course course) {
        int sectionCount = course.getWeeks();
        List<Section> sectionList = new ArrayList<>();
        if (sectionCount == 0) {
            sectionList.add(createSection(course.getCourseId(), 0));
        }

        for (int section = 1; section <= sectionCount; section++) {
            sectionList.add(createSection(course.getCourseId(), section));
        }
        return sectionList;
    }

    private Section createSection(Long courseId, int section) {
        List<VideoBrief> videoBriefList = courseRepository.getVideoBrief(courseId, section);
        int weekDuration = videoBriefList.stream()
                .mapToInt(VideoBrief::getVideoDuration)
                .sum();
        int currentWeekDuration = videoBriefList.stream()
                .mapToInt(vb -> vb.isCompleted() ? vb.getVideoDuration() : vb.getLastDuration())
                .sum();
        boolean completed = videoBriefList.stream()
                .allMatch(VideoBrief::isCompleted);
        return Section.builder()
                .section(section)
                .currentWeekDuration(currentWeekDuration)
                .completed(completed)
                .weekDuration(weekDuration)
                .videos(videoBriefList)
                .build();
    }
}
