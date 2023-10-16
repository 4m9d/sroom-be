package com.m9d.sroom.course;

import com.m9d.sroom.common.dto.*;
import com.m9d.sroom.common.dto.CourseVideoDto;
import com.m9d.sroom.course.dto.VideoInfoForSchedule;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.dto.PlaylistPageResult;
import com.m9d.sroom.ai.service.GPTService;
import com.m9d.sroom.lecture.dto.response.Section;
import com.m9d.sroom.lecture.dto.response.VideoWatchInfo;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.playlist.PlaylistDto;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.playlist.repository.PlaylistRepository;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.video.VideoDto;
import com.m9d.sroom.video.repository.VideoRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.youtube.YoutubeApi;
import com.m9d.sroom.youtube.YoutubeUtil;
import com.m9d.sroom.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.*;
import static com.m9d.sroom.util.DateUtil.*;
import static com.m9d.sroom.youtube.YoutubeUtil.DEFAULT_INDEX_COUNT;

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
    private final YoutubeUtil youtubeUtil;

    private final GPTService gptService;

    public CourseService(VideoRepository videoRepository, CourseRepository courseRepository,
                         CourseVideoRepository courseVideoRepository, CourseQuizRepository courseQuizRepository, LectureRepository lectureRepository,
                         PlaylistRepository playlistRepository, PlaylistVideoRepository playlistVideoRepository,
                         DateUtil dateUtil, YoutubeApi youtubeApi, YoutubeUtil youtubeUtil, GPTService gptService) {
        this.videoRepository = videoRepository;
        this.courseRepository = courseRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.lectureRepository = lectureRepository;
        this.playlistRepository = playlistRepository;
        this.playlistVideoRepository = playlistVideoRepository;
        this.dateUtil = dateUtil;
        this.youtubeApi = youtubeApi;
        this.youtubeUtil = youtubeUtil;
        this.gptService = gptService;
    }


    public MyCourses getMyCourses(Long memberId) {
        List<CourseDto> latestCourseListDto = courseRepository.getLatestOrderByMemberId(memberId);
        List<CourseInfo> courseInfoList = getCourseInfoList(latestCourseListDto);
        int unfinishedCourseCount = getUnfinishedCourseCount(latestCourseListDto);
        int courseCount = latestCourseListDto.size();
        int completionRate = (int) ((float) (courseCount - unfinishedCourseCount) / courseCount * 100);

        return MyCourses.builder()
                .unfinishedCourse(unfinishedCourseCount)
                .completionRate(completionRate)
                .courses(courseInfoList)
                .build();
    }

    public List<CourseInfo> getCourseInfoList(List<CourseDto> latestCourseListDto) {
        List<CourseInfo> courseInfoList = new ArrayList<>();

        for (CourseDto courseDto : latestCourseListDto) {
            Long courseId = courseDto.getCourseId();
            List<CourseVideoDto> courseVideoDtoList = courseVideoRepository.getListByCourseId(courseId);
            int progress;
            int videoCount = courseVideoDtoList.size();
            int completedVideoCount = (int) courseVideoDtoList.stream()
                    .filter(CourseVideoDto::isComplete)
                    .count();

            if (videoCount > 1) {
                progress = (int) ((double) completedVideoCount / videoCount * 100);
            }
            else {
                CourseVideoDto courseVideoDto = courseVideoDtoList.get(0);
                progress = (courseVideoDto.getMaxDuration() * 100) /
                        videoRepository.getById(courseVideoDto.getVideoId()).getDuration();
            }

            CourseInfo courseInfo = CourseInfo.builder()
                    .courseId(courseId)
                    .courseTitle(courseDto.getCourseTitle())
                    .thumbnail(courseDto.getThumbnail())
                    .channels(String.join(", ", lectureRepository.getChannelSetByCourseId(courseId)))
                    .lastViewTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(courseDto.getLastViewTime()))
                    .totalVideoCount(videoCount)
                    .completedVideoCount(completedVideoCount)
                    .progress(progress)
                    .build();

            courseInfoList.add(courseInfo);
        }

        return courseInfoList;
    }

    public int getUnfinishedCourseCount(List<CourseDto> courseDtoInfoList) {

        int unfinishedCourseCount = 0;

        for (int i = 0; i < courseDtoInfoList.size(); i++) {
            if (courseDtoInfoList.get(i).getProgress() < 100) {
                unfinishedCourseCount++;
            }
        }

        return unfinishedCourseCount;
    }

    public void requestToFastApi(VideoDto videoDto) {
        log.info("request to AI server successfully. videoCode = {}, title = {}", videoDto.getVideoCode(), videoDto.getTitle());

        if (videoDto.getMaterialStatus() == null || videoDto.getMaterialStatus() == MaterialStatus.NO_REQUEST.getValue()) {
            gptService.requestToFastApi(videoDto.getVideoCode(), videoDto.getTitle());
            videoDto.setMaterialStatus(MaterialStatus.CREATING.getValue());
            videoRepository.updateById(videoDto.getVideoId(), videoDto);
        }
    }

    @Transactional
    public EnrolledCourseInfo saveCourseWithPlaylist(Long memberId, NewLecture newLecture, boolean useSchedule, PlaylistDto playlistDto) {
        log.info("course inserted. member = {}, lectureCode = {}", memberId, newLecture.getLectureCode());
        CourseDto courseDto = saveCourse(memberId, newLecture, useSchedule, playlistDto);

        LectureDto lectureDto = lectureRepository.save(LectureDto.builder()
                .memberId(memberId)
                .courseId(courseDto.getCourseId())
                .sourceId(playlistDto.getPlaylistId())
                .channel(playlistDto.getChannel())
                .playlist(true)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        saveScheduledVideoListForCourse(memberId, courseDto.getCourseId(), lectureDto.getId(), newLecture, playlistDto.getPlaylistId(), useSchedule);

        return EnrolledCourseInfo.builder()
                .title(playlistDto.getTitle())
                .courseId(courseDto.getCourseId())
                .lectureId(lectureDto.getId())
                .build();
    }

    private CourseDto saveCourse(Long memberId, NewLecture newLecture, boolean useSchedule, PlaylistDto playlistDto) {
        Integer weeks = null;
        Integer dailyTargetTime = null;
        Date expectedEndDate = null;

        if (useSchedule) {
            validateScheduleField(newLecture);
            expectedEndDate = dateUtil.convertStringToDate(newLecture.getExpectedEndDate());
            weeks = newLecture.getScheduling().size();
            dailyTargetTime = newLecture.getDailyTargetTime();
        }

        return courseRepository.save(CourseDto.builder()
                .memberId(memberId)
                .courseTitle(playlistDto.getTitle())
                .weeks(weeks)
                .scheduled(useSchedule)
                .duration(playlistDto.getDuration())
                .thumbnail(playlistDto.getThumbnail())
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

        int videoIndex = 1;
        for (VideoDto videoDto : videoRepository.getListByPlaylistId(playlistId)) {
            if (useSchedule && videoCount > newLecture.getScheduling().get(week)) {
                week++;
                section++;
                videoCount = 1;
            }
            courseVideoRepository.save(CourseVideoDto.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .lectureId(lectureId)
                    .videoId(videoDto.getVideoId())
                    .section(section)
                    .videoIndex(videoIndex++)
                    .lectureIndex(ENROLL_LECTURE_INDEX)
                    .summaryId(videoDto.getSummaryId())
                    .build());
            videoCount++;
        }
    }

    @Transactional
    public EnrolledCourseInfo saveCourseWithVideo(Long memberId, NewLecture newLecture, boolean useSchedule) {
        log.info("course inserted. member = {}, lectureCode = {}", memberId, newLecture.getLectureCode());
        VideoDto videoDto = getVideoAndRequestToAiServer(newLecture.getLectureCode());

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

        CourseDto courseDto = courseRepository.save(CourseDto.builder()
                .memberId(memberId)
                .courseTitle(videoDto.getTitle())
                .duration(videoDto.getDuration())
                .thumbnail(videoDto.getThumbnail())
                .scheduled(useSchedule)
                .weeks(weeks)
                .expectedEndDate(expectedEndDate)
                .dailyTargetTime(dailyTargetTime)
                .build());

        LectureDto lectureDto = lectureRepository.save(LectureDto.builder()
                .memberId(memberId)
                .courseId(courseDto.getCourseId())
                .sourceId(videoDto.getVideoId())
                .channel(videoDto.getChannel())
                .playlist(false)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .build());

        courseVideoRepository.save(CourseVideoDto.builder()
                .memberId(memberId)
                .courseId(courseDto.getCourseId())
                .videoId(videoDto.getVideoId())
                .section(section)
                .videoIndex(ENROLL_VIDEO_INDEX)
                .lectureIndex(ENROLL_LECTURE_INDEX)
                .lectureId(lectureDto.getId())
                .summaryId(videoDto.getSummaryId())
                .build());

        return EnrolledCourseInfo.builder()
                .title(videoDto.getTitle())
                .courseId(courseDto.getCourseId())
                .lectureId(lectureDto.getId())
                .build();
    }

    private void validateScheduleField(NewLecture newLecture) {
        if (newLecture.getDailyTargetTime() == 0 ||
                newLecture.getExpectedEndDate() == null) {
            throw new InvalidParameterException("스케줄 필드를 적절히 입력해주세요");
        }
    }

    public PlaylistDto getPlaylistWithUpdate(String playlistCode) {
        Optional<PlaylistDto> playlistOptional = playlistRepository.findByCode(playlistCode);

        if (playlistOptional.isPresent() &&
                dateUtil.hasRecentUpdate(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            return playlistOptional.get();
        } else {
            PlaylistDto playlistDto = youtubeUtil.getPlaylistWithBlocking(playlistCode);
            if (playlistOptional.isEmpty()) {
                playlistDto = playlistRepository.save(playlistDto);
            } else {
                playlistDto.setAccumulatedRating(playlistOptional.get().getAccumulatedRating());
                playlistDto.setReviewCount(playlistOptional.get().getReviewCount());
                playlistDto = playlistRepository.updateById(playlistOptional.get().getPlaylistId(), playlistDto);
            }
            playlistVideoRepository.deleteByPlaylistId(playlistDto.getPlaylistId());
            playlistDto.setDuration(putPlaylistItemAndGetPlaylistDuration(playlistDto));
            return playlistRepository.updateById(playlistDto.getPlaylistId(), playlistDto);
        }

    }

    public int putPlaylistItemAndGetPlaylistDuration(PlaylistDto playlistDto) {
        String nextPageToken = null;
        int playlistDuration = 0;
        int pageCount = (int) Math.ceil((double) playlistDto.getVideoCount() / DEFAULT_INDEX_COUNT);
        for (int i = 0; i < pageCount; i++) {
            PlaylistPageResult result = putPlaylistItemPerPage(playlistDto.getPlaylistCode(),
                    playlistDto.getPlaylistId(), nextPageToken);
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

        List<VideoDto> videoDtoList = getVideoList(playlistVideoVo);
        int totalDurationPerPage = videoDtoList.stream().mapToInt(VideoDto::getDuration).sum();
        savePlaylistVideoList(playlistId, videoDtoList, playlistVideoVo.getItems());

        return new PlaylistPageResult(playlistVideoVo.getNextPageToken(), totalDurationPerPage);
    }

    private List<VideoDto> getVideoList(PlaylistVideoVo playlistVideoVo) {
        List<CompletableFuture<VideoDto>> futureVideos = new ArrayList<>();
        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (youtubeUtil.isPrivacyStatusUnusable(itemVo)) {
                continue;
            }
            CompletableFuture<VideoDto> videoFuture = getVideoWithUpdateAsync(itemVo.getSnippet().getResourceId().getVideoId())
                    .thenApply(video -> {
                        requestToFastApi(video);
                        return video;
                    });
            futureVideos.add(videoFuture);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureVideos.toArray(new CompletableFuture[0]));
        CompletableFuture<List<VideoDto>> allVideoFuture = allFutures.thenApply(v ->
                futureVideos.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
        return allVideoFuture.join();
    }

    public void savePlaylistVideoList(Long playlistId, List<VideoDto> videoDtos, List<PlaylistVideoItemVo> items) {
        for (int i = 0; i < videoDtos.size(); i++) {
            playlistVideoRepository.save(PlaylistVideoDto.builder()
                    .playlistId(playlistId)
                    .videoId(videoDtos
                            .get(i)
                            .getVideoId())
                    .videoIndex(items
                            .get(i)
                            .getSnippet()
                            .getPosition() + 1)
                    .build());
        }
    }

    @Transactional
    public EnrolledCourseInfo addPlaylistInCourse(Long courseId, PlaylistDto playlistDto) {
        CourseDto courseDto = courseRepository.getById(courseId);
        int lastLectureIndex = lectureRepository.getListByCourseId(courseDto.getCourseId())
                .stream()
                .mapToInt(LectureDto::getLectureIndex)
                .max()
                .orElse(0);

        LectureDto lectureDto = lectureRepository.save(LectureDto.builder()
                .courseId(courseDto.getCourseId())
                .memberId(courseDto.getMemberId())
                .sourceId(playlistDto.getPlaylistId())
                .playlist(true)
                .lectureIndex(lastLectureIndex + 1)
                .channel(playlistDto.getChannel())
                .build());

        saveCourseVideoList(courseDto, playlistDto.getPlaylistId(), lectureDto.getId(), lastLectureIndex + 1);

        if (courseDto.isScheduled()) {
            scheduleVideos(courseDto);
        }
        courseDto.setDuration(courseDto.getDuration() + playlistDto.getDuration());
        courseRepository.updateById(courseDto.getCourseId(), courseDto);
        return EnrolledCourseInfo.builder()
                .title(courseDto.getCourseTitle())
                .courseId(courseDto.getCourseId())
                .lectureId(lectureDto.getId())
                .build();
    }

    private void saveCourseVideoList(CourseDto courseDto, Long playlistId, Long lectureId, int lectureIndex) {
        int videoIndex = courseVideoRepository.getListByCourseId(courseDto.getCourseId()).stream()
                .mapToInt(CourseVideoDto::getVideoIndex)
                .max()
                .orElse(0) + 1;

        for (VideoDto videoDto : videoRepository.getListByPlaylistId(playlistId)) {
            courseVideoRepository.save(CourseVideoDto.builder()
                    .memberId(courseDto.getMemberId())
                    .courseId(courseDto.getCourseId())
                    .videoId(videoDto.getVideoId())
                    .section(ENROLL_DEFAULT_SECTION_NO_SCHEDULE)
                    .videoIndex(videoIndex++)
                    .lectureIndex(lectureIndex)
                    .lectureId(lectureId)
                    .summaryId(videoDto.getSummaryId())
                    .build());
        }
    }

    public VideoDto getVideoAndRequestToAiServer(String videoCode) {
        VideoDto videoDto = getVideoWithUpdateAsync(videoCode).join();
        requestToFastApi(videoDto);
        return videoDto;
    }

    @Async
    public CompletableFuture<VideoDto> getVideoWithUpdateAsync(String videoCode) {
        Optional<VideoDto> videoOptional = videoRepository.findByCode(videoCode);
        log.debug("optional status = {}, videoCode = {}", videoOptional.isPresent(), videoCode);
        CompletableFuture<VideoDto> result;

        if (videoOptional.isPresent() &&
                DateUtil.hasRecentUpdate(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            return CompletableFuture.completedFuture(videoOptional.get());
        } else {
            result = CompletableFuture.supplyAsync(() -> {
                VideoDto videoDto = youtubeUtil.getVideoWithBlocking(videoCode);
                return youtubeUtil.saveOrUpdateVideo(videoCode, videoDto);
            });
        }
        return result;
    }

    @Transactional
    public EnrolledCourseInfo addVideoInCourse(Long courseId, VideoDto videoDto) {
        CourseDto courseDto = courseRepository.getById(courseId);
        int lastLectureIndex = lectureRepository.getListByCourseId(courseDto.getCourseId()).stream()
                .mapToInt(LectureDto::getLectureIndex)
                .max()
                .orElse(0);

        LectureDto lectureDto = lectureRepository.save(LectureDto.builder()
                .memberId(courseDto.getMemberId())
                .courseId(courseDto.getCourseId())
                .sourceId(videoDto.getVideoId())
                .playlist(false)
                .lectureIndex(lastLectureIndex + 1)
                .channel(videoDto.getChannel())
                .build());

        saveCourseVideo(courseDto, videoDto, lectureDto.getId(), lastLectureIndex + 1);

        if (courseDto.isScheduled()) {
            scheduleVideos(courseDto);
        }
        courseDto.setDuration(courseDto.getDuration() + videoDto.getDuration());
        courseRepository.updateById(courseDto.getCourseId(), courseDto);

        return EnrolledCourseInfo.builder()
                .title(videoDto.getTitle())
                .courseId(courseDto.getCourseId())
                .lectureId(lectureDto.getId())
                .build();
    }

    private void saveCourseVideo(CourseDto courseDto, VideoDto videoDto, Long lectureId, int lectureIndex) {
        int lastVideoIndex = courseVideoRepository.getListByCourseId(courseDto.getCourseId()).stream()
                .mapToInt(CourseVideoDto::getVideoIndex)
                .max()
                .orElse(0);

        courseVideoRepository.save(CourseVideoDto.builder()
                .memberId(courseDto.getMemberId())
                .courseId(courseDto.getCourseId())
                .videoId(videoDto.getVideoId())
                .section(ENROLL_DEFAULT_SECTION_NO_SCHEDULE)
                .videoIndex(lastVideoIndex + 1)
                .lectureIndex(lectureIndex)
                .lectureId(lectureId)
                .summaryId(videoDto.getSummaryId())
                .build());
    }

    private void scheduleVideos(CourseDto courseDto) {
        Date startDate = courseDto.getStartDate();
        int dailyTargetTimeForSecond = courseDto.getDailyTargetTime() * SECONDS_IN_MINUTE;
        int weeklyTargetTimeForSecond = dailyTargetTimeForSecond * DAYS_IN_WEEK;
        int section = ENROLL_DEFAULT_SECTION_SCHEDULE;
        int currentSectionTime = 0;

        List<VideoInfoForSchedule> videoInfoForScheduleList = courseVideoRepository.getInfoForScheduleByCourseId(courseDto.getCourseId());
        int lastSectionTime = 0;

        for (VideoInfoForSchedule videoInfo : videoInfoForScheduleList) {
            if (currentSectionTime + (videoInfo.getDuration() / 2) > weeklyTargetTimeForSecond) {
                section++;
                currentSectionTime = 0;
            }

            currentSectionTime += videoInfo.getDuration();
            lastSectionTime = currentSectionTime;

            videoInfo.getCourseVideoDto().setSection(section);
            courseVideoRepository.updateById(videoInfo.getCourseVideoDto().getCourseVideoId(), videoInfo.getCourseVideoDto());
        }

        int lastSectionDays = (int) Math.ceil((double) lastSectionTime / dailyTargetTimeForSecond);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, (section - 1) * DAYS_IN_WEEK + lastSectionDays);

        courseDto.setScheduled(true);
        courseDto.setWeeks(section);
        courseDto.setExpectedEndDate(calendar.getTime());
        courseRepository.updateById(courseDto.getCourseId(), courseDto);
    }

    @Transactional
    public CourseDetail getCourseDetail(Long memberId, Long courseId) {
        boolean isValid = validateCourseId(memberId, courseId);
        if (!isValid) {
            throw new CourseNotMatchException();
        }

        CourseDto courseDto = courseRepository.getById(courseId);
        Set<String> channels = lectureRepository.getListByCourseId(courseId).stream()
                .map(LectureDto::getChannel)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Section> sections = getSectionList(courseDto);
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
                .courseTitle(courseDto.getCourseTitle())
                .useSchedule(courseDto.isScheduled())
                .channels(String.join(", ", channels))
                .courseDuration(courseDto.getDuration())
                .currentDuration(currentDuration)
                .totalVideoCount(videoCount)
                .thumbnail(courseDto.getThumbnail())
                .completedVideoCount(completedVideoCount)
                .progress((int) ((double) currentDuration / courseDto.getDuration() * 100))
                .lastViewVideo(courseVideoRepository.getLastInfoByCourseId(courseId))
                .sections(sections)
                .build();
    }

    @Transactional
    public void deleteCourse(Long memberId, Long courseId) {
        validateCourseId(memberId, courseId);

        courseRepository.deleteById(courseId);
        courseVideoRepository.deleteByCourseId(courseId);
        lectureRepository.deleteByCourseId(courseId);
        courseQuizRepository.deleteByCourseId(courseId);
    }

    public boolean validateCourseId(Long memberId, Long courseId) {
        CourseDto courseDto = courseRepository.getById(courseId);
        return memberId.equals(courseDto.getMemberId());
    }

    private List<Section> getSectionList(CourseDto courseDto) {
        List<Section> sectionList = new ArrayList<>();
        if (courseDto.getWeeks() == 0) {
            sectionList.add(getSection(courseDto.getCourseId(), 0));
        } else {
            for (int section = 1; section <= courseDto.getWeeks(); section++) {
                sectionList.add(getSection(courseDto.getCourseId(), section));
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
