package com.m9d.sroom.lecture.service;

import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.global.mapper.*;
import com.m9d.sroom.lecture.dto.VideoCompletionStatus;
import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.request.LectureTimeRecord;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.dto.response.LectureResponse;
import com.m9d.sroom.lecture.exception.TwoOnlyParamTrueException;
import com.m9d.sroom.lecture.exception.VideoIndexParamException;
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import com.m9d.sroom.repository.course.CourseRepository;
import com.m9d.sroom.repository.coursedailylog.CourseDailyLogRepository;
import com.m9d.sroom.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.repository.lecture.LectureRepository;
import com.m9d.sroom.repository.member.MemberRepository;
import com.m9d.sroom.repository.playlist.PlaylistRepository;
import com.m9d.sroom.repository.review.ReviewRepository;
import com.m9d.sroom.repository.video.VideoRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeApi;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.SearchReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchItemVo;
import com.m9d.sroom.util.youtube.vo.search.SearchSnippetVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.course.constant.CourseConstant.VIDEO_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.lecture.constant.LectureConstant.*;
import static com.m9d.sroom.util.youtube.YoutubeUtil.*;

@Slf4j
@Service
public class LectureService {
    private final PlaylistRepository playlistRepository;
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;
    private final CourseVideoRepository courseVideoRepository;
    private final MemberRepository memberRepository;
    private final CourseDailyLogRepository courseDailyLogRepository;
    private final LectureRepository lectureRepository;
    private final YoutubeUtil youtubeUtil;
    private final YoutubeApi youtubeApi;
    private final DateUtil dateUtil;

    public LectureService(CourseRepository courseRepository, ReviewRepository reviewRepository,
                          MemberRepository memberRepository, PlaylistRepository playlistRepository,
                          VideoRepository videoRepository, LectureRepository lectureRepository, YoutubeUtil youtubeUtil,
                          CourseVideoRepository courseVideoRepository, CourseDailyLogRepository courseDailyLogRepository,
                          YoutubeApi youtubeApi, DateUtil dateUtil) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.courseVideoRepository = courseVideoRepository;
        this.memberRepository = memberRepository;
        this.courseDailyLogRepository = courseDailyLogRepository;
        this.playlistRepository = playlistRepository;
        this.videoRepository = videoRepository;
        this.youtubeUtil = youtubeUtil;
        this.youtubeApi = youtubeApi;
        this.dateUtil = dateUtil;
    }


    @Transactional
    public KeywordSearch searchByKeyword(Long memberId, KeywordSearchParam keywordSearchParam) {
        log.info("lecture keyword search. memberId = {}, keyword : {}", memberId, keywordSearchParam.getKeyword());
        Mono<SearchVo> searchVoMono = getSearchVoMono(keywordSearchParam);
        Set<String> enrolledLectureSet = getEnrolledLectures(memberId);

        SearchVo searchVo = youtubeUtil.safeGetVo(searchVoMono);
        String nextPageToken = Optional.of(searchVo)
                .map(SearchVo::getNextPageToken)
                .orElse(null);

        return KeywordSearch.builder()
                .nextPageToken(nextPageToken)
                .resultPerPage(searchVo.getPageInfo().getResultsPerPage())
                .lectures(getSearchedLectureList(searchVo, enrolledLectureSet))
                .build();
    }

    private Mono<SearchVo> getSearchVoMono(KeywordSearchParam keywordSearchParam) {
        return youtubeApi.getSearchVo(SearchReq.builder()
                .keyword(URLEncoder.encode(keywordSearchParam.getKeyword(), StandardCharsets.UTF_8))
                .filter(keywordSearchParam.getFilter())
                .limit(keywordSearchParam.getLimit())
                .pageToken(keywordSearchParam.getNext_page_token())
                .build());
    }

    public List<LectureResponse> getSearchedLectureList(SearchVo searchVo, Set<String> enrolledLectureSet) {
        List<CompletableFuture<LectureResponse>> futures = searchVo
                .getItems().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> getLecture(enrolledLectureSet, item)))
                .collect(Collectors.toList());
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private LectureResponse getLecture(Set<String> enrolledLectureSet, SearchItemVo item) {
        SearchSnippetVo snippetVo = item.getSnippet();
        String lectureCode;
        String description;
        long viewCount = -1L;
        int videoCount = 1;

        boolean isPlaylist = item.getId().getKind().equals(JSONNODE_TYPE_PLAYLIST);
        if (isPlaylist) {
            lectureCode = item.getId().getPlaylistId();
            Playlist playlist = getSearchedPlaylistLast(lectureCode);
            videoCount = playlist.getVideoCount();
            description = playlist.getDescription();
        } else {
            lectureCode = item.getId().getVideoId();
            Video video = getSearchedVideoLast(lectureCode);
            if (video.getViewCount() != null) {
                viewCount = video.getViewCount();
            }
            description = video.getDescription();
        }

        return LectureResponse.builder()
                .lectureTitle(unescapeHtml(snippetVo.getTitle()))
                .description(unescapeHtml(description))
                .channel(unescapeHtml(snippetVo.getChannelTitle()))
                .lectureCode(lectureCode)
                .enrolled(enrolledLectureSet.contains(lectureCode))
                .publishedAt(dateUtil.convertISOToString(snippetVo.getPublishTime()))
                .playlist(isPlaylist)
                .lectureCount(videoCount)
                .viewCount(viewCount)
                .thumbnail(youtubeUtil.selectThumbnailInVo(snippetVo.getThumbnails()))
                .build();
    }

    private Playlist getSearchedPlaylistLast(String playlistCode) {
        Optional<Playlist> playlistOptional = playlistRepository.findByCode(playlistCode);

        if (playlistOptional.isPresent() &&
                dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            return playlistOptional.get();
        } else {
            return youtubeUtil.getPlaylistWithBlocking(playlistCode);
        }
    }

    @Transactional
    public Object getLectureDetail(Long memberId, boolean isPlaylist, String lectureCode, LectureDetailParam lectureDetailParam) {
        validateLectureDetailParam(isPlaylist, lectureDetailParam);
        log.info("lecture detail request. memberId = {}, lectureCode = {}", memberId, lectureCode);

        if (!isPlaylist && !lectureDetailParam.isReview_only()) {
            return getVideoDetail(memberId, lectureCode, lectureDetailParam.getReview_limit());
        }

        if (isPlaylist && !lectureDetailParam.isIndex_only() && !lectureDetailParam.isReview_only()) {
            return getPlaylistDetail(memberId, lectureCode, lectureDetailParam.getReview_limit());
        }

        if (lectureDetailParam.isIndex_only()) {
            return getPlaylistItemList(lectureCode);
        }

        return reviewRepository.getBriefListByCode(lectureCode, lectureDetailParam.getReview_offset(),
                lectureDetailParam.getReview_limit());
    }

    public void validateLectureDetailParam(boolean isPlaylist, LectureDetailParam lectureDetailParam) {
        if (lectureDetailParam.isIndex_only() && lectureDetailParam.isReview_only()) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && lectureDetailParam.isIndex_only()) {
            throw new VideoIndexParamException();
        }
    }

    private IndexInfo getPlaylistItemList(String playlistCode) {
        AtomicInteger totalDurationSeconds = new AtomicInteger(0);
        List<Index> indexList = new ArrayList<>();

        String nextPageToken = null;
        int pageCount = MAX_PLAYLIST_ITEM / DEFAULT_INDEX_COUNT;


        for (int i = 0; i < pageCount; i++) {
            PlaylistVideoVo playlistVideoVo = youtubeUtil.getPlaylistItemWithBlocking(playlistCode, nextPageToken, DEFAULT_INDEX_COUNT);
            pageCount = playlistVideoVo.getPageInfo().getTotalResults() / DEFAULT_INDEX_COUNT + 1;
            nextPageToken = Optional.of(playlistVideoVo)
                    .map(PlaylistVideoVo::getNextPageToken)
                    .orElse(null);
            indexList.addAll(getIndexList(playlistVideoVo));

            if (nextPageToken == null) {
                break;
            }
        }

        for (Index index : indexList) {
            totalDurationSeconds.addAndGet(index.getDuration());
        }

        return IndexInfo.builder()
                .indexList(indexList)
                .duration(totalDurationSeconds.get())
                .lectureCount(indexList.size())
                .build();
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String playlistCode, int reviewLimit) {
        Mono<PlaylistVo> playlistVoMono = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                .playlistCode(playlistCode)
                .build());

        Set<String> enrolledPlaylistSet = playlistRepository.getCodeSetByMemberId(memberId);
        List<CourseBrief> courseBriefList = courseRepository.getBriefListByMemberId(memberId);
        List<ReviewBrief> reviewList = reviewRepository.getBriefListByCode(playlistCode, DEFAULT_REVIEW_OFFSET, reviewLimit);
        Playlist playlist = youtubeUtil.getPlaylistFromMono(playlistVoMono);

        return PlaylistDetail.builder()
                .lectureCode(playlistCode)
                .lectureTitle(playlist.getTitle())
                .channel(playlist.getChannel())
                .description(playlist.getDescription())
                .playlist(true)
                .publishedAt(playlist.getPublishedAt().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .enrolled(enrolledPlaylistSet.contains(playlistCode))
                .thumbnail(playlist.getThumbnail())
                .reviews(reviewList)
                .reviewCount(reviewList.size())
                .rating(calculateAverageRating(reviewList))
                .courses(courseBriefList)
                .build();
    }

    private List<Index> getIndexList(PlaylistVideoVo playlistVideoVo) {
        List<CompletableFuture<Index>> futureList = new ArrayList<>();

        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (youtubeUtil.isPrivacyStatusUnusable(itemVo)) {
                continue;
            }
            futureList.add(CompletableFuture.supplyAsync(() ->
                    getIndex(itemVo
                                    .getSnippet()
                                    .getPosition(),
                            itemVo.getSnippet()
                                    .getResourceId()
                                    .getVideoId())));
        }

        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Index getIndex(int index, String videoCode) {
        Video video = getSearchedVideoLast(videoCode);

        return Index.builder()
                .index(index)
                .lectureTitle(unescapeHtml(video.getTitle()))
                .thumbnail(video.getThumbnail())
                .duration(video.getDuration())
                .membership(video.isMembership())
                .build();
    }

    private Video getSearchedVideoLast(String videoCode) {
        Optional<Video> videoOptional = videoRepository.findByCode(videoCode);

        if (videoOptional.isPresent() &&
                dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            return videoOptional.get();
        } else {
            return youtubeUtil.getVideoWithBlocking(videoCode);
        }
    }

    public VideoDetail getVideoDetail(Long memberId, String videoCode, int reviewLimit) {
        Mono<VideoVo> videoVoMono = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(videoCode)
                .build());

        Set<String> enrolledVideoSet = videoRepository.getCodeSetByMemberId(memberId);
        List<ReviewBrief> reviewList = reviewRepository.getBriefListByCode(videoCode, DEFAULT_REVIEW_OFFSET, reviewLimit);
        List<CourseBrief> courseBriefList = courseRepository.getBriefListByMemberId(memberId);

        Video video;
        try {
            video = youtubeUtil.getVideoFromMono(videoVoMono);
        } catch (IndexOutOfBoundsException e) {
            log.warn("존재하지 않는 영상입니다. video code = {}", videoCode);
            throw new VideoNotFoundException();
        }

        boolean isMembership = false;
        long viewCount = -1;

        if (video.getViewCount() == null) {
            isMembership = true;
        } else {
            viewCount = video.getViewCount();
        }

        IndexInfo indexInfo = IndexInfo.builder()
                .indexList(List.of(new Index(0, video.getThumbnail(), video.getTitle(), video.getDuration(), isMembership)))
                .build();

        return VideoDetail.builder()
                .lectureCode(videoCode)
                .lectureTitle(unescapeHtml(video.getTitle()))
                .channel(video.getChannel())
                .description(unescapeHtml(video.getDescription()))
                .duration(video.getDuration())
                .playlist(false)
                .enrolled(enrolledVideoSet.contains(videoCode))
                .viewCount(viewCount)
                .publishedAt(video.getPublishedAt().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .thumbnail(video.getThumbnail())
                .reviews(reviewList)
                .reviewCount(reviewList.size())
                .rating(calculateAverageRating(reviewList))
                .courses(courseBriefList)
                .membership(isMembership)
                .indexes(indexInfo)
                .build();
    }

    private double calculateAverageRating(List<ReviewBrief> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        return reviews.stream()
                .mapToInt(ReviewBrief::getSubmittedRating)
                .average()
                .orElse(0.0);
    }

    public String unescapeHtml(String input) {
        return HtmlUtils.htmlUnescape(input);
    }

    @Transactional
    public Recommendations getRecommendations(Long memberId) {
        HashSet<RecommendLecture> recommendLectureHashSet = new HashSet<>();
        List<RecommendLecture> recommendLectureList = new ArrayList<>();
        List<RecommendLecture> topRatedVideos = getTopRatedVideos();
        List<RecommendLecture> topRatedPlaylists = getTopRatedPlaylists();
        List<RecommendLecture> recommendLecturesByChannel = getRecommendsByChannel(memberId);

        Set<String> enrolledLectureSet = getEnrolledLectures(memberId);

        recommendLectureHashSet.addAll(topRatedVideos);
        recommendLectureHashSet.addAll(topRatedPlaylists);
        recommendLectureHashSet.addAll(recommendLecturesByChannel);

        for (String lectureCode : enrolledLectureSet) {
            recommendLectureHashSet.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
        }

        recommendLectureList.addAll(recommendLectureHashSet);
        Collections.shuffle(recommendLectureList);

        Recommendations recommendations = Recommendations.builder()
                .recommendations(recommendLectureList)
                .build();

        return recommendations;
    }

    public List<RecommendLecture> getTopRatedVideos() {
        return getRecommendLectures(videoRepository.getTopRatedOrder(5));
    }

    public List<RecommendLecture> getTopRatedPlaylists() {
        return getRecommendLectures(playlistRepository.getTopRatedOrder(5));
    }


    public List<RecommendLecture> getRecommendsByChannel(Long memberId) {
        List<RecommendLecture> recommendLecturesByChannel = new ArrayList<>();
        List<String> channels = getMostEnrolledChannels(memberId);

        final int SELECT_BY_RANDOM_LIMIT = 1;
        final int SELECT_BY_PUBLISH_DATE_LIMIT = 2;
        final int SELECT_BY_VIEWED_LIMIT = 3;

        for (String channelName : channels) {
            List<Object> lectures = new ArrayList<>();

            lectures.addAll(videoRepository.getRandomByChannel(channelName, SELECT_BY_RANDOM_LIMIT));
            lectures.addAll(playlistRepository.getRandomByChannel(channelName, SELECT_BY_RANDOM_LIMIT));

            lectures.addAll(videoRepository.getViewCountOrderByChannel(channelName, SELECT_BY_VIEWED_LIMIT));
            lectures.addAll(playlistRepository.getViewCountOrderByChannel(channelName, SELECT_BY_VIEWED_LIMIT));

            lectures.addAll(videoRepository.getLatestOrderByChannel(channelName, SELECT_BY_PUBLISH_DATE_LIMIT));
            lectures.addAll(playlistRepository.getLatestOrderByChannel(channelName, SELECT_BY_PUBLISH_DATE_LIMIT));

            recommendLecturesByChannel.addAll(getRecommendLectures(lectures));
        }

        return recommendLecturesByChannel;
    }

    public List<RecommendLecture> getRecommendLectures(List<?> lectures) {
        List<RecommendLecture> recommendLectures = new ArrayList<>();

        for (Object lecture : lectures) {
            if (lecture instanceof Video) {
                Video video = (Video) lecture;
                recommendLectures.add(RecommendLecture.builder()
                        .lectureTitle(video.getTitle())
                        .description(video.getDescription())
                        .channel(video.getChannel())
                        .lectureCode(video.getVideoCode())
                        .isPlaylist(false)
                        .rating((double) video.getAccumulatedRating() / video.getReviewCount())
                        .reviewCount(video.getReviewCount())
                        .thumbnail(video.getThumbnail())
                        .build());
            } else if (lecture instanceof Playlist) {
                Playlist playlist = (Playlist) lecture;
                recommendLectures.add(RecommendLecture.builder()
                        .lectureTitle(playlist.getTitle())
                        .description(playlist.getDescription())
                        .channel(playlist.getChannel())
                        .lectureCode(playlist.getPlaylistCode())
                        .isPlaylist(true)
                        .rating((double) playlist.getAccumulatedRating() / playlist.getReviewCount())
                        .reviewCount(playlist.getReviewCount())
                        .thumbnail(playlist.getThumbnail())
                        .build());
            }
        }
        return recommendLectures;
    }


    public Set<String> getEnrolledLectures(Long memberId) {
        Set<String> lectureSet = new HashSet<>();
        lectureSet.addAll(videoRepository.getCodeSetByMemberId(memberId));
        lectureSet.addAll(playlistRepository.getCodeSetByMemberId(memberId));
        return lectureSet;
    }

    public List<String> getMostEnrolledChannels(Long memberId) {
        return lectureRepository.getChannelListOrderByCount(memberId);
    }


    @Transactional
    public LectureStatus updateLectureTime(Long memberId, Long courseVideoId, LectureTimeRecord record, boolean isMarkedAsCompleted) {
        CourseVideo courseVideo = getCourseVideo(memberId, courseVideoId);
        int timeGap = record.getViewDuration() - courseVideo.getMaxDuration();

        VideoCompletionStatus status = getVideoCompletionStatus(record.getViewDuration(), timeGap, courseVideo, isMarkedAsCompleted);

        if (!status.getRewound()) {
            updateCourseDailyLog(memberId, courseVideo.getCourseId(), timeGap, status);
            Member member = memberRepository.getById(memberId);
            member.setTotalLearningTime(Math.max(timeGap, 0) + member.getTotalLearningTime());
            memberRepository.updateById(memberId, member);
        }

        if (status.getCompletedNow()) {
            updateCourseProgress(memberId, courseVideo.getCourseId(), 1);
        }

        if (status.getFullyWatched()) {
            updateLastViewVideoToNext(courseVideo.getCourseId(), courseVideo.getVideoIndex());
        }

        courseVideo.setMaxDuration(Math.max(record.getViewDuration(), courseVideo.getMaxDuration()));
        courseVideo.setStartTime(record.getViewDuration());
        courseVideo.setComplete(status.getCompleted());
        courseVideo.setLastViewTime(new Timestamp(System.currentTimeMillis()));
        courseVideoRepository.updateById(courseVideoId, courseVideo);

        return LectureStatus.builder()
                .courseVideoId(courseVideoId)
                .viewDuration(record.getViewDuration())
                .complete(status.getCompleted())
                .build();
    }

    private CourseVideo getCourseVideo(Long memberId, Long courseVideoId) {
        CourseVideo courseVideo = courseVideoRepository.findById(courseVideoId)
                .orElseThrow(CourseVideoNotFoundException::new);

        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }

        return courseVideo;
    }

    private VideoCompletionStatus getVideoCompletionStatus(int newDuration, int timeGap, CourseVideo courseVideo, boolean isMarkedAsCompleted) {
        VideoCompletionStatus status = new VideoCompletionStatus();
        status.setRewound(timeGap <= 0);
        status.setCompletedNow(false);

        Video video = videoRepository.findById(courseVideo.getVideoId())
                .orElseThrow(VideoNotFoundException::new);

        if (courseVideo.isComplete()) {
            status.setCompleted(true);
        } else {
            status.setCompleted(false);

            boolean currVideoComplete =
                    (newDuration / (double) video.getDuration()) > MINIMUM_VIEW_PERCENT_FOR_COMPLETION
                            || isMarkedAsCompleted;
            if (currVideoComplete) {
                status.setCompleted(true);
                status.setCompletedNow(true);
            }
        }

        if (newDuration >= video.getDuration() - LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS) {
            status.setFullyWatched(true);
            status.setCompleted(true);
        } else {
            status.setFullyWatched(false);
        }

        return status;
    }

    private void updateCourseProgress(Long memberId, Long courseId, int newCompletedVideoCount) {
        Course course = courseRepository.getById(courseId);
        course.setProgress((courseVideoRepository.countCompletedByCourseId(courseId) + newCompletedVideoCount) * 100
                / courseVideoRepository.countByCourseId(courseId));
        courseRepository.updateById(courseId, course);

        if (course.getProgress() == 100) {
            Member member = memberRepository.getById(memberId);
            member.setCompletionRate(memberRepository.countCompletedCourseById(memberId) * 100
                    / memberRepository.countCourseById(memberId));
            memberRepository.updateById(memberId, member);
        }
    }

    private void updateCourseDailyLog(Long memberId, Long courseId, int timeGap, VideoCompletionStatus videoStatus) {
        Optional<CourseDailyLog> dailyLogOptional = courseDailyLogRepository.findByCourseIdAndDate(courseId,
                Date.valueOf(LocalDate.now()));
        int learningTimeToAdd = Math.max(timeGap, 0);
        int lectureCountToAdd = videoStatus.getCompletedNow() ? 1 : 0;

        if (dailyLogOptional.isEmpty()) {
            CourseDailyLog initialDailyLog = CourseDailyLog.builder()
                    .memberId(memberId)
                    .courseId(courseId)
                    .dailyLogDate(Date.valueOf(LocalDate.now()))
                    .learningTime(learningTimeToAdd)
                    .quizCount(0)
                    .lectureCount(lectureCountToAdd)
                    .build();
            courseDailyLogRepository.save(initialDailyLog);
        } else {
            CourseDailyLog dailyLog = dailyLogOptional.get();
            dailyLog.setLearningTime(dailyLog.getLearningTime() + learningTimeToAdd);
            dailyLog.setLectureCount(dailyLog.getLectureCount() + lectureCountToAdd);
            courseDailyLogRepository.updateById(dailyLog.getCourseDailyLogId(), dailyLog);
        }
    }

    private void updateLastViewVideoToNext(Long courseId, int videoIndex) {
        Optional<CourseVideo> courseVideoOptional = courseVideoRepository.findByCourseIdAndPrevIndex(courseId, videoIndex);

        if (courseVideoOptional.isPresent()) {
            CourseVideo courseVideo = courseVideoOptional.get();
            courseVideo.setLastViewTime(
                    Timestamp.valueOf(LocalDateTime.now().plusSeconds(LAST_VIEW_TIME_ADJUSTMENT_IN_SECONDS)));
            courseVideoRepository.updateById(courseVideo.getCourseVideoId(), courseVideo);
        }
    }
}