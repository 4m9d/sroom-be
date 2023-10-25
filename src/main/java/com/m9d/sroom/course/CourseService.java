package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.*;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.ai.AiService;
import com.m9d.sroom.common.repository.course.CourseRepository;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.common.repository.playlist.PlaylistRepository;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.api.YoutubeApi;
import com.m9d.sroom.youtube.YoutubeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class CourseService {
    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final LectureRepository lectureRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final DateUtil dateUtil;
    private final YoutubeApi youtubeApi;
    private final YoutubeService youtubeService;

    private final AiService aiService;

    public CourseService(VideoRepository videoRepository, CourseRepository courseRepository,
                         CourseVideoRepository courseVideoRepository, CourseQuizRepository courseQuizRepository, LectureRepository lectureRepository,
                         PlaylistRepository playlistRepository, PlaylistVideoRepository playlistVideoRepository,
                         DateUtil dateUtil, YoutubeApi youtubeApi, YoutubeService youtubeService, AiService aiService) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.lectureRepository = lectureRepository;
        this.playlistRepository = playlistRepository;
        this.playlistVideoRepository = playlistVideoRepository;
        this.dateUtil = dateUtil;
        this.youtubeApi = youtubeApi;
        this.youtubeService = youtubeService;
        this.aiService = aiService;
    }


    public MyCourses getMyCourses(Long memberId) {
        List<CourseEntity> latestCourseList = courseRepository.getLatestOrderByMemberId(memberId);
        List<CourseInfo> courseInfoList = getCourseInfoList(latestCourseList);
        int unfinishedCourseCount = getUnfinishedCourseCount(latestCourseList);
        int courseCount = latestCourseList.size();
        int completionRate = (int) ((float) (courseCount - unfinishedCourseCount) / courseCount * 100);

        return MyCourses.builder()
                .unfinishedCourse(unfinishedCourseCount)
                .completionRate(completionRate)
                .courses(courseInfoList)
                .build();
    }

    public List<CourseInfo> getCourseInfoList(List<CourseEntity> latestCourseList) {
        List<CourseInfo> courseInfoList = new ArrayList<>();

        for (CourseEntity course : latestCourseList) {
            Long courseId = course.getCourseId();
            List<CourseVideoEntity> courseVideoList = courseVideoRepository.getListByCourseId(courseId);
            int progress;
            int videoCount = courseVideoList.size();
            int completedVideoCount = (int) courseVideoList.stream()
                    .filter(CourseVideoEntity::isComplete)
                    .count();

            if (videoCount > 1) {
                progress = (int) ((double) completedVideoCount / videoCount * 100);
            }
            else {
                CourseVideoEntity courseVideo = courseVideoList.get(0);
                progress = (courseVideo.getMaxDuration() * 100) /
                        videoRepository.getById(courseVideo.getVideoId()).getDuration();
            }

            CourseInfo courseInfo = CourseInfo.builder()
                    .courseId(courseId)
                    .courseTitle(course.getCourseTitle())
                    .thumbnail(course.getThumbnail())
                    .channels(String.join(", ", lectureRepository.getChannelSetByCourseId(courseId)))
                    .lastViewTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(course.getLastViewTime()))
                    .totalVideoCount(videoCount)
                    .completedVideoCount(completedVideoCount)
                    .progress(progress)
                    .build();

            courseInfoList.add(courseInfo);
        }

        return courseInfoList;
    }

    public int getUnfinishedCourseCount(List<CourseEntity> courseInfoList) {

        int unfinishedCourseCount = 0;

        for (int i = 0; i < courseInfoList.size(); i++) {
            if (courseInfoList.get(i).getProgress() < 100) {
                unfinishedCourseCount++;
            }
        }

        return unfinishedCourseCount;
    }

//    public void requestToFastApi(VideoEntity video) {
//        log.info("request to AI server successfully. videoCode = {}, title = {}", video.getVideoCode(), video.getTitle());
//
//        if (video.getMaterialStatus() == null || video.getMaterialStatus() == MaterialStatus.NO_REQUEST.getValue()) {
//            aiService.requestToFastApi(video.getVideoCode(), video.getTitle());
//            video.setMaterialStatus(MaterialStatus.CREATING.getValue());
//            videoRepository.updateById(video.getVideoId(), video);
//        }
//    }

//    @Transactional
//    public EnrolledCourseInfo saveCourseWithPlaylist(Long memberId, NewLecture newLecture, boolean useSchedule, PlaylistEntity playlist) {
//        log.info("course inserted. member = {}, lectureCode = {}", memberId, newLecture.getLectureCode());
//        CourseEntity course = saveCourse(memberId, newLecture, useSchedule, playlist);
//
//        LectureEntity lecture = lectureRepository.save(LectureEntity.builder()
//                .memberId(memberId)
//                .courseId(course.getCourseId())
//                .sourceId(playlist.getPlaylistId())
//                .channel(playlist.getChannel())
//                .playlist(true)
//                .lectureIndex(ENROLL_LECTURE_INDEX)
//                .build());
//
//        saveScheduledVideoListForCourse(memberId, course.getCourseId(), lecture.getId(), newLecture, playlist.getPlaylistId(), useSchedule);
//
//        return EnrolledCourseInfo.builder()
//                .title(playlist.getTitle())
//                .courseId(course.getCourseId())
//                .lectureId(lecture.getId())
//                .build();
//    }

//    private CourseEntity saveCourse(Long memberId, NewLecture newLecture, boolean useSchedule, PlaylistEntity playlist) {
//        Integer weeks = null;
//        Integer dailyTargetTime = null;
//        Date expectedEndDate = null;
//
//        if (useSchedule) {
//            validateScheduleField(newLecture);
//            expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndDate());
//            weeks = newLecture.getScheduling().size();
//            dailyTargetTime = newLecture.getDailyTargetTime();
//        }
//
//        return courseRepository.save(CourseEntity.builder()
//                .memberId(memberId)
//                .courseTitle(playlist.getTitle())
//                .weeks(weeks)
//                .scheduled(useSchedule)
//                .duration(playlist.getDuration())
//                .thumbnail(playlist.getThumbnail())
//                .dailyTargetTime(dailyTargetTime)
//                .expectedEndDate(expectedEndDate)
//                .build());
//    }

//    private void saveScheduledVideoListForCourse(Long memberId, Long courseId, Long lectureId, NewLecture newLecture, Long playlistId, boolean useSchedule) {
//        int videoCount = 1;
//        int week = 0;
//        int section = ENROLL_DEFAULT_SECTION_NO_SCHEDULE;
//        if (useSchedule) {
//            section = ENROLL_DEFAULT_SECTION_SCHEDULE;
//        }
//
//        int videoIndex = 1;
//        for (VideoEntity video : videoRepository.getListByPlaylistId(playlistId)) {
//            if (useSchedule && videoCount > newLecture.getScheduling().get(week)) {
//                week++;
//                section++;
//                videoCount = 1;
//            }
//            courseVideoRepository.save(CourseVideoEntity.builder()
//                    .memberId(memberId)
//                    .courseId(courseId)
//                    .lectureId(lectureId)
//                    .videoId(video.getVideoId())
//                    .section(section)
//                    .videoIndex(videoIndex++)
//                    .lectureIndex(ENROLL_LECTURE_INDEX)
//                    .summaryId(video.getSummaryId())
//                    .build());
//            videoCount++;
//        }
//    }
//
//    @Transactional
//    public EnrolledCourseInfo saveCourseWithVideo(Long memberId, NewLecture newLecture, boolean useSchedule) {
//        log.info("course inserted. member = {}, lectureCode = {}", memberId, newLecture.getLectureCode());
//        VideoEntity video = getVideoAndRequestToAiServer(newLecture.getLectureCode());
//
//        Date expectedEndDate = null;
//        Integer dailyTargetTime = null;
//        Integer weeks = null;
//        int section = ENROLL_DEFAULT_SECTION_NO_SCHEDULE;
//
//        if (useSchedule) {
//            validateScheduleField(newLecture);
//            expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndDate());
//            dailyTargetTime = newLecture.getDailyTargetTime();
//            weeks = newLecture.getScheduling().size();
//            section = ENROLL_DEFAULT_SECTION_SCHEDULE;
//        }
//
//        CourseEntity course = courseRepository.save(CourseEntity.builder()
//                .memberId(memberId)
//                .courseTitle(video.getTitle())
//                .duration(video.getDuration())
//                .thumbnail(video.getThumbnail())
//                .scheduled(useSchedule)
//                .weeks(weeks)
//                .expectedEndDate(expectedEndDate)
//                .dailyTargetTime(dailyTargetTime)
//                .build());
//
//        LectureEntity lecture = lectureRepository.save(LectureEntity.builder()
//                .memberId(memberId)
//                .courseId(course.getCourseId())
//                .sourceId(video.getVideoId())
//                .channel(video.getChannel())
//                .playlist(false)
//                .lectureIndex(ENROLL_LECTURE_INDEX)
//                .build());
//
//        courseVideoRepository.save(CourseVideoEntity.builder()
//                .memberId(memberId)
//                .courseId(course.getCourseId())
//                .videoId(video.getVideoId())
//                .section(section)
//                .videoIndex(ENROLL_VIDEO_INDEX)
//                .lectureIndex(ENROLL_LECTURE_INDEX)
//                .lectureId(lecture.getId())
//                .summaryId(video.getSummaryId())
//                .build());
//
//        return EnrolledCourseInfo.builder()
//                .title(video.getTitle())
//                .courseId(course.getCourseId())
//                .lectureId(lecture.getId())
//                .build();
//    }

    private void validateScheduleField(NewLecture newLecture) {
        if (newLecture.getDailyTargetTime() == 0 ||
                newLecture.getExpectedEndDate() == null) {
            throw new InvalidParameterException("스케줄 필드를 적절히 입력해주세요");
        }
    }

//    public PlaylistEntity getPlaylistWithUpdate(String playlistCode) {
//        Optional<PlaylistEntity> playlistOptional = playlistRepository.findByCode(playlistCode);
//
//        if (playlistOptional.isPresent() &&
//                dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
//            return playlistOptional.get();
//        } else {
//            PlaylistEntity playlist = youtubeService.getPlaylistWithBlocking(playlistCode);
//            if (playlistOptional.isEmpty()) {
//                playlist = playlistRepository.save(playlist);
//            } else {
//                playlist.setAccumulatedRating(playlistOptional.get().getAccumulatedRating());
//                playlist.setReviewCount(playlistOptional.get().getReviewCount());
//                playlist = playlistRepository.updateById(playlistOptional.get().getPlaylistId(), playlist);
//            }
//            playlistVideoRepository.deleteByPlaylistId(playlist.getPlaylistId());
//            playlist.setDuration(putPlaylistItemAndGetPlaylistDuration(playlist));
//            return playlistRepository.updateById(playlist.getPlaylistId(), playlist);
//        }
//
//    }

//    public int putPlaylistItemAndGetPlaylistDuration(PlaylistEntity playlist) {
//        String nextPageToken = null;
//        int playlistDuration = 0;
//        int pageCount = (int) Math.ceil((double) playlist.getVideoCount() / DEFAULT_INDEX_COUNT);
//        for (int i = 0; i < pageCount; i++) {
//            PlaylistPageResult result = putPlaylistItemPerPage(playlist.getPlaylistCode(),
//                    playlist.getPlaylistId(), nextPageToken);
//            nextPageToken = result.getNextPageToken();
//            playlistDuration += result.getTotalDurationPerPage();
//        }
//        return playlistDuration;
//    }

//    private PlaylistPageResult putPlaylistItemPerPage(String lectureCode, Long playlistId, String nextPageToken) {
//        Mono<PlaylistVideoDto> playlistVideoVoMono = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
//                .playlistCode(lectureCode)
//                .limit(DEFAULT_INDEX_COUNT)
//                .nextPageToken(nextPageToken)
//                .build());
//        PlaylistVideoDto playlistVideoVo = youtubeService.safeGetVo(playlistVideoVoMono);
//
//        List<VideoEntity> videoList = getVideoList(playlistVideoVo);
//        int totalDurationPerPage = videoList.stream().mapToInt(VideoEntity::getDuration).sum();
//        savePlaylistVideoList(playlistId, videoList, playlistVideoVo.getItems());
//
//        return new PlaylistPageResult(playlistVideoVo.getNextPageToken(), totalDurationPerPage);
//    }

//    private List<VideoEntity> getVideoList(PlaylistVideoDto playlistVideoVo) {
//        List<CompletableFuture<VideoEntity>> futureVideos = new ArrayList<>();
//        for (PlaylistVideoItemDto itemVo : playlistVideoVo.getItems()) {
//            if (youtubeService.isPrivacyStatusUnusable(itemVo)) {
//                continue;
//            }
//            CompletableFuture<VideoEntity> videoFuture = getVideoWithUpdateAsync(itemVo.getSnippet().getResourceId().getVideoId())
//                    .thenApply(video -> {
//                        requestToFastApi(video);
//                        return video;
//                    });
//            futureVideos.add(videoFuture);
//        }
//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureVideos.toArray(new CompletableFuture[0]));
//        CompletableFuture<List<VideoEntity>> allVideoFuture = allFutures.thenApply(v ->
//                futureVideos.stream()
//                        .map(CompletableFuture::join)
//                        .collect(Collectors.toList()));
//        return allVideoFuture.join();
//    }
//
//    public void savePlaylistVideoList(Long playlistId, List<VideoEntity> videos, List<PlaylistVideoItemDto> items) {
//        for (int i = 0; i < videos.size(); i++) {
//            playlistVideoRepository.save(PlaylistVideoEntity.builder()
//                    .playlistId(playlistId)
//                    .videoId(videos
//                            .get(i)
//                            .getVideoId())
//                    .videoIndex(items
//                            .get(i)
//                            .getSnippet()
//                            .getPosition() + 1)
//                    .build());
//        }
//    }

//    @Transactional
//    public EnrolledCourseInfo addPlaylistInCourse(Long courseId, PlaylistEntity playlist) {
//        CourseEntity course = courseRepository.getById(courseId);
//        int lastLectureIndex = lectureRepository.getListByCourseId(course.getCourseId())
//                .stream()
//                .mapToInt(LectureEntity::getLectureIndex)
//                .max()
//                .orElse(0);
//
//        LectureEntity lecture = lectureRepository.save(LectureEntity.builder()
//                .courseId(course.getCourseId())
//                .memberId(course.getMemberId())
//                .sourceId(playlist.getPlaylistId())
//                .playlist(true)
//                .lectureIndex(lastLectureIndex + 1)
//                .channel(playlist.getChannel())
//                .build());
//
//        saveCourseVideoList(course, playlist.getPlaylistId(), lecture.getId(), lastLectureIndex + 1);
//
//        if (course.isScheduled()) {
//            scheduleVideos(course);
//        }
//        course.setDuration(course.getDuration() + playlist.getDuration());
//        courseRepository.updateById(course.getCourseId(), course);
//        return EnrolledCourseInfo.builder()
//                .title(course.getCourseTitle())
//                .courseId(course.getCourseId())
//                .lectureId(lecture.getId())
//                .build();
//    }

//    private void saveCourseVideoList(CourseEntity course, Long playlistId, Long lectureId, int lectureIndex) {
//        int videoIndex = courseVideoRepository.getListByCourseId(course.getCourseId()).stream()
//                .mapToInt(CourseVideoEntity::getVideoIndex)
//                .max()
//                .orElse(0) + 1;
//
//        for (VideoEntity video : videoRepository.getListByPlaylistId(playlistId)) {
//            courseVideoRepository.save(CourseVideoEntity.builder()
//                    .memberId(course.getMemberId())
//                    .courseId(course.getCourseId())
//                    .videoId(video.getVideoId())
//                    .section(ENROLL_DEFAULT_SECTION_NO_SCHEDULE)
//                    .videoIndex(videoIndex++)
//                    .lectureIndex(lectureIndex)
//                    .lectureId(lectureId)
//                    .summaryId(video.getSummaryId())
//                    .build());
//        }
//    }
//
//    public VideoEntity getVideoAndRequestToAiServer(String videoCode) {
//        VideoEntity video = getVideoWithUpdateAsync(videoCode).join();
//        requestToFastApi(video);
//        return video;
//    }

//    @Async
//    public CompletableFuture<VideoEntity> getVideoWithUpdateAsync(String videoCode) {
//        Optional<VideoEntity> videoOptional = videoRepository.findByCode(videoCode);
//        log.debug("optional status = {}, videoCode = {}", videoOptional.isPresent(), videoCode);
//        CompletableFuture<VideoEntity> result;
//
//        if (videoOptional.isPresent() &&
//                dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
//            return CompletableFuture.completedFuture(videoOptional.get());
//        } else {
//            result = CompletableFuture.supplyAsync(() -> {
//                VideoEntity video = youtubeService.getVideoWithBlocking(videoCode);
//                return youtubeService.saveOrUpdateVideo(videoCode, video);
//            });
//        }
//        return result;
//    }

//    @Transactional
//    public EnrolledCourseInfo addVideoInCourse(Long courseId, VideoEntity video) {
//        CourseEntity course = courseRepository.getById(courseId);
//        int lastLectureIndex = lectureRepository.getListByCourseId(course.getCourseId()).stream()
//                .mapToInt(LectureEntity::getLectureIndex)
//                .max()
//                .orElse(0);
//
//        LectureEntity lecture = lectureRepository.save(LectureEntity.builder()
//                .memberId(course.getMemberId())
//                .courseId(course.getCourseId())
//                .sourceId(video.getVideoId())
//                .playlist(false)
//                .lectureIndex(lastLectureIndex + 1)
//                .channel(video.getChannel())
//                .build());
//
//        saveCourseVideo(course, video, lecture.getId(), lastLectureIndex + 1);
//
//        if (course.isScheduled()) {
//            scheduleVideos(course);
//        }
//        course.setDuration(course.getDuration() + video.getDuration());
//        courseRepository.updateById(course.getCourseId(), course);
//
//        return EnrolledCourseInfo.builder()
//                .title(video.getTitle())
//                .courseId(course.getCourseId())
//                .lectureId(lecture.getId())
//                .build();
//    }

//    private void saveCourseVideo(CourseEntity course, VideoEntity video, Long lectureId, int lectureIndex) {
//        int lastVideoIndex = courseVideoRepository.getListByCourseId(course.getCourseId()).stream()
//                .mapToInt(CourseVideoEntity::getVideoIndex)
//                .max()
//                .orElse(0);
//
//        courseVideoRepository.save(CourseVideoEntity.builder()
//                .memberId(course.getMemberId())
//                .courseId(course.getCourseId())
//                .videoId(video.getVideoId())
//                .section(ENROLL_DEFAULT_SECTION_NO_SCHEDULE)
//                .videoIndex(lastVideoIndex + 1)
//                .lectureIndex(lectureIndex)
//                .lectureId(lectureId)
//                .summaryId(video.getSummaryId())
//                .build());
//    }

//    private void scheduleVideos(CourseEntity course) {
//        Date startDate = course.getStartDate();
//        int dailyTargetTimeForSecond = course.getDailyTargetTime() * SECONDS_IN_MINUTE;
//        int weeklyTargetTimeForSecond = dailyTargetTimeForSecond * DAYS_IN_WEEK;
//        int section = ENROLL_DEFAULT_SECTION_SCHEDULE;
//        int currentSectionTime = 0;
//
//        List<VideoInfoForSchedule> videoInfoForScheduleList = courseVideoRepository.getInfoForScheduleByCourseId(course.getCourseId());
//        int lastSectionTime = 0;
//
//        for (VideoInfoForSchedule videoInfo : videoInfoForScheduleList) {
//            if (currentSectionTime + (videoInfo.getDuration() / 2) > weeklyTargetTimeForSecond) {
//                section++;
//                currentSectionTime = 0;
//            }
//
//            currentSectionTime += videoInfo.getDuration();
//            lastSectionTime = currentSectionTime;
//
//            videoInfo.getCourseVideo().setSection(section);
//            courseVideoRepository.updateById(videoInfo.getCourseVideo().getCourseVideoId(), videoInfo.getCourseVideo());
//        }
//
//        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTimeForSecond);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(startDate);
//        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);
//
//        course.setScheduled(true);
//        course.setWeeks(section);
//        course.setExpectedEndDate(calendar.getTime());
//        courseRepository.updateById(course.getCourseId(), course);
//    }

//    @Transactional
//    public CourseDetail getCourseDetail(Long memberId, Long courseId) {
//        boolean isValid = validateCourseId(memberId, courseId);
//        if (!isValid) {
//            throw new CourseNotMatchException();
//        }
//
//        CourseEntity course = courseRepository.getById(courseId);
//        Set<String> channels = lectureRepository.getListByCourseId(courseId).stream()
//                .map(LectureEntity::getChannel)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//
//        List<Section> sections = getSectionList(course);
//        int currentDuration = sections.stream()
//                .mapToInt(Section::getCurrentWeekDuration)
//                .sum();
//        int videoCount = sections.stream()
//                .mapToInt(section -> section.getVideos().size())
//                .sum();
//        int completedVideoCount = sections.stream()
//                .mapToInt(section -> (int) section.getVideos().stream()
//                        .filter(VideoWatchInfo::isCompleted)
//                        .count())
//                .sum();
//        return CourseDetail.builder()
//                .courseId(courseId)
//                .courseTitle(course.getCourseTitle())
//                .useSchedule(course.isScheduled())
//                .channels(String.join(", ", channels))
//                .courseDuration(course.getDuration())
//                .currentDuration(currentDuration)
//                .totalVideoCount(videoCount)
//                .thumbnail(course.getThumbnail())
//                .completedVideoCount(completedVideoCount)
//                .progress((int) ((double) currentDuration / course.getDuration() * 100))
//                .lastViewVideo(courseVideoRepository.getLastInfoByCourseId(courseId))
//                .sections(sections)
//                .build();
//    }

    @Transactional
    public void deleteCourse(Long memberId, Long courseId) {
        validateCourseId(memberId, courseId);

        courseRepository.deleteById(courseId);
        courseVideoRepository.deleteByCourseId(courseId);
        lectureRepository.deleteByCourseId(courseId);
        courseQuizRepository.deleteByCourseId(courseId);
    }

    public boolean validateCourseId(Long memberId, Long courseId) {
        CourseEntity course = courseRepository.getById(courseId);
        return memberId.equals(course.getMemberId());
    }

//    private List<Section> getSectionList(CourseEntity course) {
//        List<Section> sectionList = new ArrayList<>();
//        if (course.getWeeks() == 0) {
//            sectionList.add(getSection(course.getCourseId(), 0));
//        } else {
//            for (int section = 1; section <= course.getWeeks(); section++) {
//                sectionList.add(getSection(course.getCourseId(), section));
//            }
//        }
//        return sectionList;
//    }

//    private Section getSection(Long courseId, int section) {
//        List<VideoWatchInfo> videoWatchInfoList = courseVideoRepository.getWatchInfoListByCourseIdAndSection(courseId, section);
//        int weekDuration = videoWatchInfoList.stream()
//                .mapToInt(VideoWatchInfo::getVideoDuration)
//                .sum();
//        int currentWeekDuration = videoWatchInfoList.stream()
//                .mapToInt(vb -> vb.isCompleted() ? vb.getVideoDuration() : vb.getLastViewDuration())
//                .sum();
//        boolean completed = videoWatchInfoList.stream()
//                .allMatch(VideoWatchInfo::isCompleted);
//        return Section.builder()
//                .section(section)
//                .currentWeekDuration(currentWeekDuration)
//                .completed(completed)
//                .weekDuration(weekDuration)
//                .videos(videoWatchInfoList)
//                .build();
//    }
}
