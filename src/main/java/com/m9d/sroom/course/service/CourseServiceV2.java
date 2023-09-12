package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.global.model.*;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.model.PlaylistPageResult;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.gpt.service.GPTService;
import com.m9d.sroom.lecture.dto.response.Section;
import com.m9d.sroom.lecture.dto.response.VideoInfoForCreateSection;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.repository.course.CourseRepository;
import com.m9d.sroom.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.repository.lecture.LectureRepository;
import com.m9d.sroom.repository.member.MemberRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.util.DateUtil.DAYS_IN_WEEK;
import static com.m9d.sroom.util.DateUtil.SECONDS_IN_MINUTE;
import static com.m9d.sroom.util.youtube.YoutubeUtil.*;

@Service
@Slf4j
public class CourseServiceV2 {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final MemberRepository memberRepository;
    private final DateUtil dateUtil;
    private final YoutubeApi youtubeApi;
    private final YoutubeUtil youtubeUtil;

    private final GPTService gptService;

    public CourseServiceV2(VideoRepository videoRepository, CourseRepository courseRepository, LectureRepository lectureRepository, CourseVideoRepository courseVideoRepository, PlaylistRepository playlistRepository, PlaylistVideoRepository playlistVideoRepository, MemberRepository memberRepository, DateUtil dateUtil, YoutubeApi youtubeApi, YoutubeUtil youtubeUtil, GPTService gptService) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.playlistRepository = playlistRepository;
        this.playlistVideoRepository = playlistVideoRepository;
        this.memberRepository = memberRepository;
        this.dateUtil = dateUtil;
        this.youtubeApi = youtubeApi;
        this.youtubeUtil = youtubeUtil;
        this.gptService = gptService;
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
            int completedLectureCount = courseRepository.getCompletedVideoCountByCourseId(courseId);

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

    public void requestToAiServer(String videoCode) {
        log.info("request to AI server successfully. videoCode = {}", videoCode);
        Integer videoMaterialStatus = videoRepository.getByCode(videoCode).getMaterialStatus();

        if (videoMaterialStatus == null || videoMaterialStatus == MaterialStatus.NO_REQUEST.getValue()) {
            gptService.requestToFastApi(videoCode);
            videoRepository.updateMaterialStatusByCode(videoCode, MaterialStatus.CREATING.getValue());
        }
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

    public EnrolledCourseInfo saveCourseWithPlaylist(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Playlist playlist = getPlaylistWithUpdate(newLecture.getLectureCode());

        Long courseId = saveCourse(memberId, newLecture, useSchedule, playlist);

        saveCourseVideoListFirst(memberId, courseId, newLecture, playlist.getPlaylistId(), useSchedule);

        Long lectureId = lectureRepository.save(Lecture.builder()
                .memberId(memberId)
                .courseId(courseId)
                .sourceId(playlist.getPlaylistId())
                .channel(playlist.getChannel())
                .playlist(true)
                .index(ENROLL_LECTURE_INDEX)
                .build());

        return EnrolledCourseInfo.builder()
                .title(playlist.getTitle())
                .courseId(courseId)
                .lectureId(lectureId)
                .build();
    }

    private void saveCourseVideoListFirst(Long memberId, Long courseId, NewLecture newLecture, Long playlistId, boolean useSchedule) {
        int videoCount = 1;
        int section = useSchedule ? 1 : 0;
        int week = 0;

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
                    .videoId(video.getVideoId())
                    .section(section)
                    .videoIndex(video.getIndex())
                    .lectureIndex(ENROLL_LECTURE_INDEX)
                    .build());
            videoCount++;
        }
    }

    private Long saveCourse(Long memberId, NewLecture newLecture, boolean useSchedule, Playlist playlist) {
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
                .duration(playlist.getDuration())
                .thumbnail(playlist.getThumbnail())
                .weeks(weeks)
                .dailyTargetTime(dailyTargetTime)
                .expectedEndTime(expectedEndDate)
                .build());
    }

    private EnrolledCourseInfo saveCourseWithVideo(Long memberId, NewLecture newLecture, boolean useSchedule) {
        Video video = safeGetVideo(newLecture.getLectureCode());

        Date expectedEndDate = null;
        Integer dailyTargetTime = null;
        Integer weeks = null;
        int section = ENROLL_DEFAULT_SECTION;

        if (useSchedule) {
            validateScheduleField(newLecture);
            expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndDate());
            dailyTargetTime = newLecture.getDailyTargetTime();
            weeks = newLecture.getScheduling().size();
            section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        }

        Long courseId = courseRepository.save(Course.builder()
                .memberId(memberId)
                .title(video.getTitle())
                .duration(video.getDuration())
                .thumbnail(video.getThumbnail())
                .scheduled(useSchedule)
                .weeks(weeks)
                .expectedEndTime(expectedEndDate)
                .dailyTargetTime(dailyTargetTime)
                .build());

        courseVideoRepository.save(CourseVideo.builder()
                .memberId(memberId)
                .courseId(courseId)
                .videoId(video.getVideoId())
                .section(section)
                .videoIndex(ENROLL_VIDEO_INDEX)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        Long lectureId = lectureRepository.save(Lecture.builder()
                .memberId(memberId)
                .courseId(courseId)
                .sourceId(video.getVideoId())
                .channel(video.getChannel())
                .playlist(false)
                .index(ENROLL_LECTURE_INDEX)
                .build());
        return EnrolledCourseInfo.builder()
                .title(video.getTitle())
                .courseId(courseId)
                .lectureId(lectureId)
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
        validateCourseId(memberId, courseId);
        boolean isPlaylist = youtubeUtil.checkIfPlaylist(newLecture.getLectureCode());
        Course course = courseRepository.getById(courseId);

        EnrolledCourseInfo enrolledCourseInfo;
        if (isPlaylist) {
            Playlist playlist = getPlaylistWithUpdate(newLecture.getLectureCode());
            enrolledCourseInfo = addPlaylistInCourse(course, playlist);
        } else {
            Video video = safeGetVideo(newLecture.getLectureCode());
            enrolledCourseInfo = addVideoInCourse(course, video);
        }
        return enrolledCourseInfo;
    }

    private Playlist getPlaylistWithUpdate(String playlistCode) {
        Optional<Playlist> playlistOptional = playlistRepository.findByCode(playlistCode);
        Playlist playlist;

        if (playlistOptional.isPresent() && dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            playlist = playlistOptional.get();
        } else {
            playlist = youtubeUtil.getPlaylistWithBlocking(playlistCode);
            if (playlistOptional.isEmpty()) {
                Long playlistId = playlistRepository.save(playlist);
                playlist.setPlaylistId(playlistId);
            } else {
                playlistRepository.updateById(playlist.getPlaylistId(), playlist);
            }
            playlistVideoRepository.deleteByPlaylistId(playlist.getPlaylistId());
            playlist.setDuration(putPlaylistItemAndGetPlaylistDuration(playlistCode, playlist.getPlaylistId(), playlist.getLectureCount()));
            playlistRepository.updateById(playlist.getPlaylistId(), playlist);
        }

        return playlist;
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
        PlaylistVideoVo playlistVideoVo = youtubeUtil.safeGetVo(playlistVideoVoMono);

        List<Video> videoList = getVideoList(playlistVideoVo);
        int totalDurationPerPage = videoList.stream().mapToInt(Video::getDuration).sum();
        savePlaylistVideoList(playlistId, videoList, playlistVideoVo.getItems());

        try {
            nextPageToken = playlistVideoVo.getNextPageToken();
        } catch (NullPointerException e) {
            nextPageToken = null;
        }

        return new PlaylistPageResult(nextPageToken, totalDurationPerPage);
    }

    private List<Video> getVideoList(PlaylistVideoVo playlistVideoVo) {
        List<CompletableFuture<Video>> futureVideos = new ArrayList<>();
        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (youtubeUtil.isPrivacyStatusUnusable(itemVo)) {
                continue;
            }
            CompletableFuture<Video> videoFuture = getVideoWithUpdateAsync(itemVo.getSnippet().getResourceId().getVideoId());
            futureVideos.add(videoFuture);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureVideos.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Video>> allVideoFuture = allFutures.thenApply(v ->
                futureVideos.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        List<Video> videoList = allVideoFuture.join();

        for (Video video : videoList) {
            requestToAiServer(video.getVideoCode());
        }

        return videoList;
    }

    public void savePlaylistVideoList(Long playlistId, List<Video> videos, List<PlaylistVideoItemVo> items) {
        for (int i = 0; i < videos.size(); i++) {
            playlistVideoRepository.save(PlaylistVideo.builder()
                    .playlistId(playlistId)
                    .videoId(videos.get(i).getVideoId())
                    .videoIndex(items.get(i).getSnippet().getPosition() + 1)
                    .build());
        }
    }

    private EnrolledCourseInfo addPlaylistInCourse(Course course, Playlist playlist) {
        int lastLectureIndex = lectureRepository.getListByCourseId(course.getCourseId())
                .stream()
                .mapToInt(Lecture::getIndex)
                .max()
                .orElse(0);

        Long lectureId = lectureRepository.save(Lecture.builder()
                .courseId(course.getCourseId())
                .memberId(course.getMemberId())
                .sourceId(playlist.getPlaylistId())
                .playlist(true)
                .index(lastLectureIndex + 1)
                .channel(playlist.getChannel())
                .build());

        saveCourseVideoList(course, playlist, lastLectureIndex + 1);

        if (course.isScheduled()) {
            scheduleVideos(course);
        }
        course.setDuration(course.getDuration() + playlist.getDuration());
        courseRepository.updateById(course.getCourseId(), course);
        return EnrolledCourseInfo.builder()
                .title(course.getTitle())
                .courseId(course.getCourseId())
                .lectureId(lectureId)
                .build();
    }

    private void saveCourseVideoList(Course course, Playlist playlist, int lectureIndex) {
        int videoIndex = courseVideoRepository.getListByCourseId(course.getCourseId()).stream()
                .mapToInt(CourseVideo::getVideoIndex)
                .max()
                .orElse(0) + 1;

        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.getListByPlaylistId(playlist.getPlaylistId());
        playlistVideoList.sort(Comparator.comparingInt(PlaylistVideo::getVideoIndex));

        for (PlaylistVideo playlistVideo : playlistVideoList) {
            courseVideoRepository.save(CourseVideo.builder()
                    .memberId(course.getMemberId())
                    .courseId(course.getCourseId())
                    .videoId(playlistVideo.getVideoId())
                    .section(ENROLL_DEFAULT_SECTION)
                    .videoIndex(videoIndex)
                    .lectureIndex(lectureIndex)
                    .build());
            videoIndex++;
        }
    }

    private Video safeGetVideo(String videoCode) {
        Video video;
        try {
            video = getVideoWithUpdateAsync(videoCode).get();
            requestToAiServer(videoCode);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return video;
    }

    @Async
    public CompletableFuture<Video> getVideoWithUpdateAsync(String videoCode) {
        Optional<Video> videoOptional = videoRepository.findByCode(videoCode);
        CompletableFuture<Video> result;

        if (videoOptional.isPresent() && dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            result = CompletableFuture.completedFuture(videoOptional.get());
        } else {
            result = CompletableFuture.supplyAsync(() -> {
                Video video = youtubeUtil.getVideoWithBlocking(videoCode);
                Long videoId = videoOptional.isPresent() ? videoRepository.update(video) : videoRepository.save(video);
                video.setVideoId(videoId);
                return video;
            });
        }
        return result;
    }

    private EnrolledCourseInfo addVideoInCourse(Course course, Video video) {
        int lastLectureIndex = lectureRepository.getListByCourseId(course.getCourseId()).stream()
                .mapToInt(Lecture::getIndex)
                .max()
                .orElse(0);

        Long lectureId = lectureRepository.save(Lecture.builder()
                .memberId(course.getMemberId())
                .courseId(course.getCourseId())
                .sourceId(video.getVideoId())
                .channel(video.getChannel())
                .playlist(false)
                .index(lastLectureIndex + 1)
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
                .lectureId(lectureId)
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
                .section(ENROLL_DEFAULT_SECTION)
                .videoIndex(lastVideoIndex + 1)
                .lectureIndex(lectureIndex)
                .build());
    }

    private void scheduleVideos(Course course) {
        Date startDate = course.getStartDate();
        int dailyTargetTimeForSecond = course.getDailyTargetTime() * SECONDS_IN_MINUTE;
        int weeklyTargetTimeForSecond = dailyTargetTimeForSecond * DAYS_IN_WEEK;
        int section = 1;
        int currentSectionTime = 0;


        List<VideoInfoForSchedule> videoInfoForScheduleList = courseVideoRepository.getVideoInfoForScheduleByCourseId(course.getCourseId());
        int lastSectionTime = 0;

        for (VideoInfoForSchedule videoInfo : videoInfoForScheduleList) {
            CourseVideo courseVideo = courseVideoRepository.getById(videoInfo.getCourseVideoId());

            if (currentSectionTime + (videoInfo.getDuration() / 2) > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += videoInfo.getDuration();
            lastSectionTime = currentSectionTime;

            courseVideo.setSection(section);
            courseVideoRepository.updateById(courseVideo.getId(), courseVideo);
        }

        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTimeForSecond);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);

        course.setWeeks(section);
        course.setExpectedEndTime(calendar.getTime());
        courseRepository.updateById(course.getCourseId(), course);
    }

    @Transactional
    public CourseDetail getCourseDetail(Long memberId, Long courseId) {
        validateCourseId(memberId, courseId);
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
                        .filter(VideoInfoForCreateSection::isCompleted)
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
                .lastViewVideo(courseVideoRepository.getLastByCourseId(courseId))
                .sections(sections)
                .build();
    }

    private List<Section> getSectionList(Course course) {
        int sectionCount = course.getWeeks();
        List<Section> sectionList = new ArrayList<>();
        if (sectionCount == 0) {
            sectionList.add(createSection(course.getCourseId(), 0));
        } else {
            for (int section = 1; section <= sectionCount; section++) {
                sectionList.add(createSection(course.getCourseId(), section));
            }
        }
        return sectionList;
    }

    private Section createSection(Long courseId, int section) {
        List<VideoInfoForCreateSection> videoInfoForCreateSectionList = courseRepository.getVideoInfoForCreateSectionByCourseIdAndSection(courseId, section);

        int weekDuration = videoInfoForCreateSectionList.stream()
                .mapToInt(VideoInfoForCreateSection::getVideoDuration)
                .sum();
        int currentWeekDuration = videoInfoForCreateSectionList.stream()
                .mapToInt(vb -> vb.isCompleted() ? vb.getVideoDuration() : vb.getLastViewDuration())
                .sum();
        boolean completed = videoInfoForCreateSectionList.stream()
                .allMatch(VideoInfoForCreateSection::isCompleted);
        return Section.builder()
                .section(section)
                .currentWeekDuration(currentWeekDuration)
                .completed(completed)
                .weekDuration(weekDuration)
                .videos(videoInfoForCreateSectionList)
                .build();
    }

    @Transactional
    public void deleteCourse(Long memberId, Long courseId) {
        validateCourseId(memberId, courseId);

        courseRepository.deleteCourseById(courseId);
        courseRepository.deleteCourseVideoByCourseId(courseId);
        courseRepository.deleteLectureByCourseId(courseId);
        courseRepository.deleteCourseQuizByCourseId(courseId);
    }

    private void validateCourseId(Long memberId, Long courseId) {
        Long actualMemberId = memberRepository.getById(memberId).getMemberId();

        if (!memberId.equals(actualMemberId)) {
            throw new CourseNotMatchException();
        }
    }
}
