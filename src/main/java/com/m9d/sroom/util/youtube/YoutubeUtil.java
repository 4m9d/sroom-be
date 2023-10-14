package com.m9d.sroom.util.youtube;

import com.m9d.sroom.global.mapper.PlaylistDto;
import com.m9d.sroom.global.mapper.VideoDto;
import com.m9d.sroom.repository.video.VideoRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import com.m9d.sroom.util.youtube.vo.global.ThumbnailVo;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistItemVo;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.video.VideoItemVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

import static com.m9d.sroom.lecture.constant.LectureConstant.*;

@Service
@Slf4j
public class YoutubeUtil {

    private final DateUtil dateUtil;
    private final YoutubeApi youtubeApi;
    private final VideoRepository videoRepository;

    public YoutubeUtil(DateUtil dateUtil, YoutubeApi youtubeApi, VideoRepository videoRepository) {
        this.dateUtil = dateUtil;
        this.youtubeApi = youtubeApi;
        this.videoRepository = videoRepository;
    }

    public static final Map<String, String> LECTURE_LIST_PARAMETERS = Map.of(
            "part", "id,snippet",
            "fields", "nextPageToken,prevPageToken,pageInfo,items(id,snippet(title,channelTitle,thumbnails,description,publishTime))"
    );

    public static final Map<String, String> VIDEO_PARAMETERS = Map.of(
            "part", "snippet,contentDetails,statistics,status",
            "fields", "pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable,license,publishAt,privacyStatus),statistics(viewCount))"
    );

    public static final Map<String, String> PLAYLIST_PARAMETERS = Map.of(
            "part", "id,snippet,status,contentDetails",
            "fields", "pageInfo,items(id,snippet(publishedAt,title,description,thumbnails,channelTitle),status,contentDetails)"
    );

    public static final Map<String, String> PLAYLIST_ITEMS_PARAMETERS = Map.of(
            "part", "snippet,status",
            "fields", "pageInfo,nextPageToken,prevPageToken,items(snippet(title,position,resourceId,thumbnails),status)"
    );

    public static final String REQUEST_METHOD_GET = HttpMethod.GET.name();
    public static final String YOUTUBE_REQUEST_CONTENT_TYPE = "application/json";
    public static final int DEFAULT_INDEX_COUNT = 50;
    public static final int MAX_PLAYLIST_ITEM = 5000;

    public static final String UNKNOWN_LANGUAGE = "unknown";

    //JsonNode
    public static final int FIRST_INDEX = 0;
    public static final String JSONNODE_PROCESSED = "processed";
    public static final String JSONNODE_PRIVATE = "private";
    public static final String JSONNODE_UNSPECIFIED = "privacyStatusUnspecified";

    public static final String JSONNODE_TYPE_PLAYLIST = "youtube#playlist";
    public static final String JSONNODE_TYPE_VIDEO = "youtube#video";

    public PlaylistDto getPlaylistWithBlocking(String playlistCode) {
        Mono<PlaylistVo> playlistVoMono = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                .playlistCode(playlistCode)
                .build());

        return getPlaylistFromMono(playlistVoMono);
    }

    public VideoDto getVideoWithBlocking(String videoCode) {
        Mono<VideoVo> videoVoMono = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(videoCode)
                .build());
        return getVideoFromMono(videoVoMono);
    }

    public PlaylistVideoVo getPlaylistItemWithBlocking(String playlistCode, String nextToken, int limit) {
        Mono<PlaylistVideoVo> playlistVideoVoMono = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
                .playlistCode(playlistCode)
                .nextPageToken(nextToken)
                .limit(limit)
                .build());
        return safeGetVo(playlistVideoVoMono);
    }

    public PlaylistDto getPlaylistFromMono(Mono<PlaylistVo> playlistVoMono) {
        PlaylistItemVo itemVo = safeGetVo(playlistVoMono).getItems().get(FIRST_INDEX);

        return PlaylistDto.builder()
                .playlistCode(itemVo.getId())
                .thumbnail(selectThumbnailInVo(itemVo.getSnippet().getThumbnails()))
                .title(itemVo.getSnippet().getTitle())
                .channel(itemVo.getSnippet().getChannelTitle())
                .description(itemVo.getSnippet().getDescription())
                .publishedAt(dateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt()))
                .videoCount(itemVo.getContentDetails().getItemCount())
                .build();
    }

    public VideoDto getVideoFromMono(Mono<VideoVo> videoVoMono) throws IndexOutOfBoundsException {
        VideoItemVo itemVo = safeGetVo(videoVoMono).getItems().get(FIRST_INDEX);

        String language;
        if (itemVo.getSnippet().getDefaultAudioLanguage() != null) {
            language = itemVo.getSnippet().getDefaultAudioLanguage();
        } else {
            language = UNKNOWN_LANGUAGE;
        }
        boolean membership = false;
        Long viewCount = itemVo.getStatistics().getViewCount();
        if (viewCount == null) {
            membership = true;
        }

        return VideoDto.builder()
                .videoCode(itemVo.getId())
                .title(itemVo.getSnippet().getTitle())
                .channel(itemVo.getSnippet().getChannelTitle())
                .description(itemVo.getSnippet().getDescription())
                .duration(dateUtil.convertISOToSeconds(itemVo.getContentDetails().getDuration()))
                .playlist(false)
                .viewCount(itemVo.getStatistics().getViewCount())
                .publishedAt(dateUtil.convertISOToTimestamp(itemVo.getSnippet().getPublishedAt()))
                .thumbnail(selectThumbnailInVo(itemVo.getSnippet().getThumbnails()))
                .language(language)
                .license(itemVo.getStatus().getLicense())
                .membership(membership)
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

    public static boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(LECTURE_CODE_START_INDEX, LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH);
        return firstTwoCharacters.equals(PLAYLIST_CODE_INDICATOR);
    }

    public String selectThumbnailInVo(ThumbnailVo thumbnailVo) {
        String selectedThumbnailUrl = "";


        if (thumbnailVo.getMedium() != null) {
            selectedThumbnailUrl = thumbnailVo.getMedium().getUrl();
        }

        if (thumbnailVo.getMaxres() != null) {
            return thumbnailVo.getMaxres().getUrl();
        }

        return selectedThumbnailUrl;
    }

    public boolean isPrivacyStatusUnusable(PlaylistVideoItemVo itemVo) {
        String privacyStatus = itemVo.getStatus().getPrivacyStatus();
        return privacyStatus.equals(JSONNODE_PRIVATE) || privacyStatus.equals(JSONNODE_UNSPECIFIED);
    }

    @Transactional
    public VideoDto saveOrUpdateVideo(String videoCode, VideoDto videoDto) {
        Optional<VideoDto> videoOptional = videoRepository.findByCode(videoCode);
        if (videoOptional.isPresent()) {
            VideoDto videoDtoOriginal = videoOptional.get();
            videoDto.setAccumulatedRating(videoDtoOriginal.getAccumulatedRating());
            videoDto.setSummaryId(videoDtoOriginal.getSummaryId());
            videoDto.setReviewCount(videoDtoOriginal.getReviewCount());
            videoDto.setChapterUse(videoDtoOriginal.isChapterUse());
            videoDto.setMaterialStatus(videoDtoOriginal.getMaterialStatus());
            videoDto.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return videoRepository.updateById(videoOptional.get().getVideoId(), videoDto);
        } else {
            return videoRepository.save(videoDto);
        }
    }
}
