package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.dto.PlaylistPageResult;
import com.m9d.sroom.global.mapper.*;
import com.m9d.sroom.gpt.service.GPTService;
import com.m9d.sroom.lecture.dto.response.Section;
import com.m9d.sroom.lecture.dto.response.VideoWatchInfo;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.repository.course.CourseRepository;
import com.m9d.sroom.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.repository.lecture.LectureRepository;
import com.m9d.sroom.repository.playlist.PlaylistRepository;
import com.m9d.sroom.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.repository.video.VideoRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeApi;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.util.DateUtil.DAYS_IN_WEEK;
import static com.m9d.sroom.util.DateUtil.SECONDS_IN_MINUTE;
import static com.m9d.sroom.util.youtube.YoutubeUtil.DEFAULT_INDEX_COUNT;

@Slf4j
public class CourseServiceV2 {
    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final LectureRepository lectureRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final DateUtil dateUtil;
    private final YoutubeApi youtubeApi;
    private final YoutubeUtil youtubeUtil;

    private final GPTService gptService;

    public CourseServiceV2(VideoRepository videoRepository, CourseRepository courseRepository,
                           CourseVideoRepository courseVideoRepository, LectureRepository lectureRepository,
                           PlaylistRepository playlistRepository, PlaylistVideoRepository playlistVideoRepository,
                           DateUtil dateUtil, YoutubeApi youtubeApi, YoutubeUtil youtubeUtil, GPTService gptService) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.lectureRepository = lectureRepository;
        this.playlistRepository = playlistRepository;
        this.playlistVideoRepository = playlistVideoRepository;
        this.dateUtil = dateUtil;
        this.youtubeApi = youtubeApi;
        this.youtubeUtil = youtubeUtil;
        this.gptService = gptService;
    }


//    public MyCourses getMyCourses(Long memberId) {
//
//        List<CourseInfo> courseInfoList = courseRepository.getCourseListByMemberId(memberId);
//        int unfinishedCourseCount = getUnfinishedCourseCount(courseInfoList);
//
//        int courseCount = courseInfoList.size();
//
//        int completionRate = (int) ((float) (courseCount - unfinishedCourseCount) / courseCount * 100);
//
//        for (int i = 0; i < courseInfoList.size(); i++) {
//
//            Long courseId = courseInfoList.get(i).getCourseId();
//            HashSet<String> channels = courseRepository.getChannelSetByCourseId(courseId);
//            int lectureCount = courseRepository.getTotalLectureCountByCourseId(courseId);
//            int completedLectureCount = courseRepository.getCompletedVideoCountByCourseId(courseId);
//
//            courseInfoList.get(i).setChannels(String.join(", ", channels));
//            courseInfoList.get(i).setTotalVideoCount(lectureCount);
//            courseInfoList.get(i).setCompletedVideoCount(completedLectureCount);
//        }
//
//        MyCourses myCourses = MyCourses.builder()
//                .unfinishedCourse(unfinishedCourseCount)
//                .completionRate(completionRate)
//                .courses(courseInfoList)
//                .build();
//
//        return myCourses;
//    }

    public int getUnfinishedCourseCount(List<CourseInfo> courseInfoList) {

        int unfinishedCourseCount = 0;

        for (int i = 0; i < courseInfoList.size(); i++) {
            if (courseInfoList.get(i).getProgress() < 100) {
                unfinishedCourseCount++;
            }
        }

        return unfinishedCourseCount;
    }

    public void requestToFastApi(String videoCode) {
        Video video = videoRepository.getByCode(videoCode);
        log.info("request to AI server successfully. videoCode = {}, title = {}", videoCode, video.getTitle());

        if (video.getMaterialStatus() == null || video.getMaterialStatus() == MaterialStatus.NO_REQUEST.getValue()) {
            gptService.requestToFastApi(videoCode);
            video.setMaterialStatus(MaterialStatus.CREATING.getValue());
            videoRepository.updateById(video.getVideoId(), video);
        }
    }

    @Transactional
    public EnrolledCourseInfo enrollCourse(Long memberId, NewLecture newLecture, boolean useSchedule) {
        log.info("course inserted. member = {}, lectureCode = {}", memberId, newLecture.getLectureCode());
        if (youtubeUtil.checkIfPlaylist(newLecture.getLectureCode())) {
            return saveCourseWithPlaylist(memberId, newLecture, useSchedule);
        } else {
            return saveCourseWithVideo(memberId, newLecture, useSchedule);
        }
    }

    public EnrolledCourseInfo saveCourseWithPlaylist(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Playlist playlist = getPlaylistWithUpdate(newLecture.getLectureCode());

        Course course = saveCourse(memberId, newLecture, useSchedule, playlist);

        Lecture lecture = lectureRepository.save(Lecture.builder()
                .memberId(memberId)
                .courseId(course.getCourseId())
                .sourceId(playlist.getPlaylistId())
                .channel(playlist.getChannel())
                .playlist(true)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        saveScheduledVideoListForCourse(memberId, course.getCourseId(), lecture.getId(), newLecture, playlist.getPlaylistId(), useSchedule);

        return EnrolledCourseInfo.builder()
                .title(playlist.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lecture.getId())
                .build();
    }

    private Course saveCourse(Long memberId, NewLecture newLecture, boolean useSchedule, Playlist playlist) {
        Integer weeks = null;
        Integer dailyTargetTime = null;
        Date expectedEndDate = null;

        if (useSchedule) {
            validateScheduleField(newLecture);
            expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndDate());
            weeks = newLecture.getScheduling().size();
            dailyTargetTime = newLecture.getDailyTargetTime();
        }

        return courseRepository.save(Course.builder()
                .memberId(memberId)
                .title(playlist.getTitle())
                .weeks(weeks)
                .dailyTargetTime(dailyTargetTime)
                .expectedEndDate(expectedEndDate)
                .build());
    }

    private void saveScheduledVideoListForCourse(Long memberId, Long courseId, Long lectureId, NewLecture newLecture, Long playlistId, boolean useSchedule) {
        int videoCount = 1;
        int week = 0;
        int section = ENROLL_DEFAULT_SECTION_NO_SCHEDULE;
        if (useSchedule) {
            section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        }

        List<Video> videoList = videoRepository.getListByPlaylistId(playlistId);
        for (Video video : videoList) {
            if (useSchedule && videoCount > newLecture.getScheduling().get(week)) {
                week++;
                section++;
                videoCount = 1;
            }
            courseVideoRepository.save(CourseVideo.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .lectureId(lectureId)
                    .videoId(video.getVideoId())
                    .section(section)
                    .videoIndex(video.getIndex())
                    .lectureIndex(ENROLL_LECTURE_INDEX)
                    .build());
            videoCount++;
        }
    }

    private EnrolledCourseInfo saveCourseWithVideo(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Video video = getVideoAndRequestToAiServer(newLecture.getLectureCode());

        Date expectedEndDate = null;
        Integer dailyTargetTime = null;
        Integer weeks = null;
        int section = ENROLL_DEFAULT_SECTION_NO_SCHEDULE;

        if (useSchedule) {
            validateScheduleField(newLecture);
            expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndDate());
            dailyTargetTime = newLecture.getDailyTargetTime();
            weeks = newLecture.getScheduling().size();
            section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        }

        Course course = courseRepository.save(Course.builder()
                .memberId(memberId)
                .title(video.getTitle())
                .duration(video.getDuration())
                .thumbnail(video.getThumbnail())
                .scheduled(useSchedule)
                .weeks(weeks)
                .expectedEndDate(expectedEndDate)
                .dailyTargetTime(dailyTargetTime)
                .build());

        courseVideoRepository.save(CourseVideo.builder()
                .memberId(memberId)
                .courseId(course.getCourseId())
                .videoId(video.getVideoId())
                .section(section)
                .videoIndex(ENROLL_VIDEO_INDEX)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        Lecture lecture = lectureRepository.save(Lecture.builder()
                .memberId(memberId)
                .courseId(course.getCourseId())
                .sourceId(video.getVideoId())
                .channel(video.getChannel())
                .playlist(false)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        return EnrolledCourseInfo.builder()
                .title(video.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lecture.getId())
                .build();
    }

    private void validateScheduleField(NewLecture newLecture) {
        if (newLecture.getDailyTargetTime() == 0 ||
                newLecture.getExpectedEndDate() == null) {
            throw new InvalidParameterException("스케줄 필드를 적절히 입력해주세요");
        }
    }

    @Transactional
    public EnrolledCourseInfo addLectureInCourse(Long memberId, Long courseId, NewLecture newLecture) {
        boolean isValid = validateCourseId(memberId, courseId);
        if (!isValid) {
            throw new CourseNotMatchException();
        }

        if (youtubeUtil.checkIfPlaylist(newLecture.getLectureCode())) {
            return addPlaylistInCourse(courseRepository.getById(courseId), getPlaylistWithUpdate(newLecture.getLectureCode()));
        } else {
            return addVideoInCourse(courseRepository.getById(courseId), getVideoAndRequestToAiServer(newLecture.getLectureCode()));
        }
    }

    private Playlist getPlaylistWithUpdate(String playlistCode) {
        Optional<Playlist> playlistOptional = playlistRepository.findByCode(playlistCode);

        if (playlistOptional.isPresent() &&
                dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            return playlistOptional.get();
        } else {
            Playlist playlist = youtubeUtil.getPlaylistWithBlocking(playlistCode);
            if (playlistOptional.isEmpty()) {
                playlist = playlistRepository.save(playlist);
            } else {
                playlist = playlistRepository.updateById(playlistOptional.get()
                        .getPlaylistId(), playlist);
            }
            playlistVideoRepository.deleteByPlaylistId(playlist.getPlaylistId());
            playlist.setDuration(putPlaylistItemAndGetPlaylistDuration(playlist));
            return playlistRepository.save(playlist);
        }

    }

    public int putPlaylistItemAndGetPlaylistDuration(Playlist playlist) {
        String nextPageToken = null;
        int playlistDuration = 0;
        int pageCount = (int) Math.ceil((double) playlist.getVideoCount() / DEFAULT_INDEX_COUNT);
        for (int i = 0; i < pageCount; i++) {
            PlaylistPageResult result = putPlaylistItemPerPage(playlist.getPlaylistCode(),
                    playlist.getPlaylistId(), nextPageToken);
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
        PlaylistVideoVo playlistVideoVo = youtubeUtil.safeGetVo(playlistVideoVoMono);

        List<Video> videoList = getVideoList(playlistVideoVo);
        int totalDurationPerPage = videoList.stream().mapToInt(Video::getDuration).sum();
        savePlaylistVideoList(playlistId, videoList, playlistVideoVo.getItems());

        return new PlaylistPageResult(playlistVideoVo.getNextPageToken(), totalDurationPerPage);
    }

    private List<Video> getVideoList(PlaylistVideoVo playlistVideoVo) {
        List<CompletableFuture<Video>> futureVideos = new ArrayList<>();
        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (youtubeUtil.isPrivacyStatusUnusable(itemVo)) {
                continue;
            }
            CompletableFuture<Video> videoFuture = getVideoWithUpdateAsync(itemVo.getSnippet().getResourceId().getVideoId())
                    .thenApply(video -> {
                        requestToFastApi(video.getVideoCode());
                        return video;
                    });
            futureVideos.add(videoFuture);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureVideos.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Video>> allVideoFuture = allFutures.thenApply(v ->
                futureVideos.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
        return allVideoFuture.join();
    }

    public void savePlaylistVideoList(Long playlistId, List<Video> videos, List<PlaylistVideoItemVo> items) {
        for (int i = 0; i < videos.size(); i++) {
            playlistVideoRepository.save(PlaylistVideo.builder()
                    .playlistId(playlistId)
                    .videoId(videos
                            .get(i)
                            .getVideoId())
                    .videoIndex(items
                            .get(i)
                            .getSnippet()
                            .getPosition() + 1)
                    .build());
        }
    }

    private EnrolledCourseInfo addPlaylistInCourse(Course course, Playlist playlist) {
        int lastLectureIndex = lectureRepository.getListByCourseId(course.getCourseId())
                .stream()
                .mapToInt(Lecture::getLectureIndex)
                .max()
                .orElse(0);

        Lecture lecture = lectureRepository.save(Lecture.builder()
                .courseId(course.getCourseId())
                .memberId(course.getMemberId())
                .sourceId(playlist.getPlaylistId())
                .playlist(true)
                .lectureIndex(lastLectureIndex + 1)
                .channel(playlist.getChannel())
                .build());

        saveCourseVideoList(course, playlist.getPlaylistId(), lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }
        course.setDuration(course.getDuration() + playlist.getDuration());
        courseRepository.updateById(course.getCourseId(), course);
        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lecture.getId())
                .build();
    }

    private void saveCourseVideoList(Course course, Long playlistId, int lectureIndex) {
        int videoIndex = courseVideoRepository.getListByCourseId(course.getCourseId()).stream()
                .mapToInt(CourseVideo::getVideoIndex)
                .max()
                .orElse(0) + 1;

        for (Video video : videoRepository.getListByPlaylistId(playlistId)) {
            courseVideoRepository.save(CourseVideo.builder()
                    .memberId(course.getMemberId())
                    .courseId(course.getCourseId())
                    .videoId(video.getVideoId())
                    .section(ENROLL_DEFAULT_SECTION_NO_SCHEDULE)
                    .videoIndex(videoIndex++)
                    .lectureIndex(lectureIndex)
                    .summaryId(video.getSummaryId())
                    .build());
        }
    }

    private Video getVideoAndRequestToAiServer(String videoCode) {
        Video video = getVideoWithUpdateAsync(videoCode).join();
        requestToFastApi(video.getVideoCode());
        return video;
    }

    @Async
    public CompletableFuture<Video> getVideoWithUpdateAsync(String videoCode) {
        Optional<Video> videoOptional = videoRepository.findByCode(videoCode);
        CompletableFuture<Video> result;

        if (videoOptional.isPresent() &&
                dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            return CompletableFuture.completedFuture(videoOptional.get());
        } else {
            result = CompletableFuture.supplyAsync(() -> {
                Video video = youtubeUtil.getVideoWithBlocking(videoCode);
                if (videoOptional.isPresent()) {
                    Video videoOriginal = videoOptional.get();
                    video.setAccumulatedRating(videoOriginal.getAccumulatedRating());
                    video.setSummaryId(videoOriginal.getSummaryId());
                    video.setReviewCount(videoOriginal.getReviewCount());
                    video.setChapterUse(videoOriginal.isChapterUse());
                    video.setMaterialStatus(videoOriginal.getMaterialStatus());
                    return videoRepository.updateById(videoOptional.get().getVideoId(), video);
                } else {
                    return videoRepository.save(video);
                }
            });
        }
        return result;
    }

    private EnrolledCourseInfo addVideoInCourse(Course course, Video video) {
        int lastLectureIndex = lectureRepository.getListByCourseId(course.getCourseId()).stream()
                .mapToInt(Lecture::getLectureIndex)
                .max()
                .orElse(0);

        Lecture lecture = lectureRepository.save(Lecture.builder()
                .memberId(course.getMemberId())
                .courseId(course.getCourseId())
                .sourceId(video.getVideoId())
                .playlist(false)
                .lectureIndex(lastLectureIndex + 1)
                .channel(video.getChannel())
                .build());

        saveCourseVideo(course, video, lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }
        course.setDuration(course.getDuration() + video.getDuration());
        courseRepository.updateById(course.getCourseId(), course);

        return EnrolledCourseInfo.builder()
                .title(video.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lecture.getId())
                .build();
    }

    private void saveCourseVideo(Course course, Video video, int lectureIndex) {
        int lastVideoIndex = courseVideoRepository.getListByCourseId(course.getCourseId()).stream()
                .mapToInt(CourseVideo::getVideoIndex)
                .max()
                .orElse(0);

        courseVideoRepository.save(CourseVideo.builder()
                .memberId(course.getMemberId())
                .courseId(course.getCourseId())
                .videoId(video.getVideoId())
                .section(ENROLL_DEFAULT_SECTION_NO_SCHEDULE)
                .videoIndex(lastVideoIndex + 1)
                .lectureIndex(lectureIndex)
                .summaryId(video.getSummaryId())
                .build());
    }

    private void scheduleVideos(Course course) {
        Date startDate = course.getStartDate();
        int dailyTargetTimeForSecond = course.getDailyTargetTime() * SECONDS_IN_MINUTE;
        int weeklyTargetTimeForSecond = dailyTargetTimeForSecond * DAYS_IN_WEEK;
        int section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        int currentSectionTime = 0;

        List<VideoInfoForSchedule> videoInfoForScheduleList = courseVideoRepository.getInfoForScheduleByCourseId(course.getCourseId());
        int lastSectionTime = 0;

        for (VideoInfoForSchedule videoInfo : videoInfoForScheduleList) {
            if (currentSectionTime + (videoInfo.getDuration() / 2) > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += videoInfo.getDuration();
            lastSectionTime = currentSectionTime;

            videoInfo.getCourseVideo().setSection(section);
            courseVideoRepository.updateById(videoInfo.getCourseVideo().getCourseId(), videoInfo.getCourseVideo());
        }

        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTimeForSecond);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);

        course.setScheduled(true);
        course.setWeeks(section);
        course.setExpectedEndDate(calendar.getTime());
        courseRepository.updateById(course.getCourseId(), course);
    }

    @Transactional
    public CourseDetail getCourseDetail(Long memberId, Long courseId) {
        boolean isValid = validateCourseId(memberId, courseId);
        if (!isValid) {
            throw new CourseNotMatchException();
        }

        Course course = courseRepository.getById(courseId);
        Set<String> channels = lectureRepository.getListByCourseId(courseId).stream()
                .map(Lecture::getChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Section> sections = getSectionList(course);
        int currentDuration = sections.stream()
                .mapToInt(Section::getCurrentWeekDuration)
                .sum();
        int videoCount = sections.stream()
                .mapToInt(section -> section.getVideos().size())
                .sum();
        int completedVideoCount = sections.stream()
                .mapToInt(section -> (int) section.getVideos().stream()
                        .filter(VideoWatchInfo::isCompleted)
                        .count())
                .sum();
        return CourseDetail.builder()
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .useSchedule(course.isScheduled())
                .channels(String.join(", ", channels))
                .courseDuration(course.getDuration())
                .currentDuration(currentDuration)
                .totalVideoCount(videoCount)
                .thumbnail(course.getThumbnail())
                .completedVideoCount(completedVideoCount)
                .progress((int) ((double) currentDuration / course.getDuration() * 100))
                .lastViewVideo(courseVideoRepository.getLastInfoByCourseId(courseId))
                .sections(sections)
                .build();
    }

//    @Transactional
//    public void deleteCourse(Long memberId, Long courseId) {
//        validateCourseId(memberId, courseId);
//
//        courseRepository.deleteCourseById(courseId);
//        courseRepository.deleteCourseVideoByCourseId(courseId);
//        courseRepository.deleteLectureByCourseId(courseId);
//        courseRepository.deleteCourseQuizByCourseId(courseId);
//    }

    private boolean validateCourseId(Long memberId, Long courseId) {
        Course course = courseRepository.getById(courseId);
        return memberId.equals(course.getMemberId());
    }

    private List<Section> getSectionList(Course course) {
        List<Section> sectionList = new ArrayList<>();
        if (course.getWeeks() == 0) {
            sectionList.add(getSection(course.getCourseId(), 0));
        } else {
            for (int section = 1; section <= course.getWeeks(); section++) {
                sectionList.add(getSection(course.getCourseId(), section));
            }
        }
        return sectionList;
    }

    private Section getSection(Long courseId, int section) {
        List<VideoWatchInfo> videoWatchInfoList = courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseId, section);
        int weekDuration = videoWatchInfoList.stream()
                .mapToInt(VideoWatchInfo::getVideoDuration)
                .sum();
        int currentWeekDuration = videoWatchInfoList.stream()
                .mapToInt(vb -> vb.isCompleted() ? vb.getVideoDuration() : vb.getLastViewDuration())
                .sum();
        boolean completed = videoWatchInfoList.stream()
                .allMatch(VideoWatchInfo::isCompleted);
        return Section.builder()
                .section(section)
                .currentWeekDuration(currentWeekDuration)
                .completed(completed)
                .weekDuration(weekDuration)
                .videos(videoWatchInfoList)
                .build();
    }
}
