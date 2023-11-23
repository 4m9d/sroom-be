package com.m9d.sroom.playlist;

import com.m9d.sroom.common.entity.PlaylistEntity;
import com.m9d.sroom.common.repository.playlist.PlaylistRepository;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.playlist.vo.Playlist;
import com.m9d.sroom.video.vo.PlaylistItem;
import com.m9d.sroom.playlist.vo.PlaylistWithItemList;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.playlist.constant.PlaylistConstant;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.video.VideoService;
import com.m9d.sroom.video.constant.VideoConstant;
import com.m9d.sroom.youtube.YoutubeMapper;
import com.m9d.sroom.youtube.vo.PlaylistItemInfo;
import com.m9d.sroom.youtube.vo.PlaylistVideoInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final YoutubeMapper youtubeService;
    private final VideoService videoService;
    private final PlaylistVideoRepository playlistVideoRepository;

    public PlaylistService(PlaylistRepository playlistRepository, YoutubeMapper youtubeService, VideoService videoService, PlaylistVideoRepository playlistVideoRepository) {
        this.playlistRepository = playlistRepository;
        this.youtubeService = youtubeService;
        this.videoService = videoService;
        this.playlistVideoRepository = playlistVideoRepository;
    }

    public Playlist getRecentPlaylist(String playlistCode) {
        Optional<PlaylistEntity> playlistEntityOptional = playlistRepository.findByCode(playlistCode);
        int reviewCount = 0;
        int accumulatedRating = 0;

        if (playlistEntityOptional.isPresent()) {
            reviewCount = playlistEntityOptional.get().getReviewCount();
            accumulatedRating = playlistEntityOptional.get().getAccumulatedRating();
            if(DateUtil.hasRecentUpdate(playlistEntityOptional.get().getUpdatedAt(), PlaylistConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
                return playlistEntityOptional.get().toPlaylist();
            }
        }
        
        return youtubeService.getPlaylist(playlistCode, reviewCount, accumulatedRating);
    }

    public PlaylistWithItemList getRecentPlaylistWithItemList(String playlistCode) {
        return new PlaylistWithItemList(getRecentPlaylist(playlistCode), getRecentPlaylistItemList(playlistCode));
    }

    private List<PlaylistItem> getRecentPlaylistItemList(String playlistCode) {
        String nextPageToken = null;
        int pageCount = PlaylistConstant.MAX_PLAYLIST_ITEM / PlaylistConstant.DEFAULT_INDEX_COUNT;

        List<PlaylistItem> playlistItemList = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            PlaylistItemInfo playlistItemInfo = youtubeService.getPlaylistItemInfo(playlistCode, nextPageToken, PlaylistConstant.DEFAULT_INDEX_COUNT);
            pageCount = playlistItemInfo.getTotalResultCount() / PlaylistConstant.DEFAULT_INDEX_COUNT + 1;
            nextPageToken = playlistItemInfo.getNextPageToken();
            playlistItemList.addAll(getPlaylistItemPerPage(playlistItemInfo));

            if (nextPageToken == null) {
                break;
            }
        }
        return playlistItemList;
    }

    private List<PlaylistItem> getPlaylistItemPerPage(PlaylistItemInfo playlistItemInfo) {
        List<CompletableFuture<PlaylistItem>> futureList = new ArrayList<>();

        for (PlaylistVideoInfo videoInfo : playlistItemInfo.getPlaylistVideoInfoList()) {
            if (videoInfo.isPrivacyStatusUnusable()) {
                continue;
            }
            futureList.add(CompletableFuture.supplyAsync(() ->
                    new PlaylistItem(videoService.getRecentVideo(videoInfo.getVideoCode()), videoInfo.getPosition())
            ));
        }

        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean putPlaylist(Playlist playlist, int playlistDuration) {
        Optional<PlaylistEntity> playlistEntityOptional = playlistRepository.findByCode(playlist.getCode());

        boolean isUpdated;

        if (playlistEntityOptional.isEmpty()) {
            playlistRepository.save(new PlaylistEntity(playlist, playlistDuration));
            isUpdated = true;
        } else if (!DateUtil.hasRecentUpdate(playlistEntityOptional.get().getUpdatedAt(), VideoConstant.VIDEO_UPDATE_THRESHOLD_HOURS)) {
            playlistRepository.updateById(playlistEntityOptional.get().getPlaylistId(),
                    playlistEntityOptional.get().updateByYoutube(playlist, playlistDuration));
            isUpdated = true;
        } else {
            isUpdated = false;
        }
        return isUpdated;
    }

    public void putPlaylistWithItemList(PlaylistWithItemList playlistWithItemList) {
        if (!putPlaylist(playlistWithItemList, playlistWithItemList.getPlaylistDuration())) {
            return;
        }
        PlaylistEntity playlistEntity = playlistRepository.getByCode(playlistWithItemList.getCode());
        playlistVideoRepository.deleteByPlaylistId(playlistEntity.getPlaylistId());

        for (PlaylistItem playlistItem : playlistWithItemList.getPlaylistItemList()) {
            videoService.putPlaylistItem(playlistEntity.getPlaylistId(), playlistItem);
        }
    }

    public EnrollContentInfo getEnrollContentInfo(String playlistCode) {
        PlaylistEntity playlistEntity = playlistRepository.getByCode(playlistCode);

        return new EnrollContentInfo(true, playlistEntity.getPlaylistId(), playlistEntity.getTitle(),
                playlistEntity.getDuration(), playlistEntity.getThumbnail(), playlistEntity.getChannel(),
                videoService.getEnrollInnerContentList(playlistEntity.getPlaylistId()));
    }

    public Set<String> getEnrolledCodeSet(Long memberId) {
        return playlistRepository.getCodeSetByMemberId(memberId);
    }

    public List<PlaylistEntity> getTopRatedPlaylists(int limit) {
        return playlistRepository.getTopRatedOrder(limit);
    }
}
