package com.m9d.sroom.playlist;

import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.playlist.repository.PlaylistRepository;
import com.m9d.sroom.playlist.repository.PlaylistSaved;
import com.m9d.sroom.youtube.YoutubeService;
import com.m9d.sroom.youtube.dto.PlaylistItemInfo;
import com.m9d.sroom.youtube.dto.PlaylistVideoInfo;
import com.m9d.sroom.video.PlaylistItem;
import com.m9d.sroom.video.PlaylistItemSaved;
import com.m9d.sroom.video.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.m9d.sroom.youtube.YoutubeConstant.*;

@Service
public class PlaylistService {

    private final YoutubeService youtubeService;
    private final VideoService videoService;
    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;

    public PlaylistService(YoutubeService youtubeService, VideoService videoService, PlaylistRepository playlistRepository, PlaylistVideoRepository playlistVideoRepository) {
        this.youtubeService = youtubeService;
        this.videoService = videoService;
        this.playlistRepository = playlistRepository;
        this.playlistVideoRepository = playlistVideoRepository;
    }

    public Playlist getPlaylist(String playlistCode) {
        Optional<PlaylistDto> playlistDtoOptional = playlistRepository.findByCode(playlistCode);

        if (playlistDtoOptional.isEmpty() || !new PlaylistSaved(playlistDtoOptional.get()).isRecentContent()) {
            return new Playlist(youtubeService.getPlaylistInfo(playlistCode));
        } else {
            return new Playlist(playlistDtoOptional.get().toPlaylistInfo());
        }
    }

    public PlaylistWithItemListSaved getplaylistWithItemListSaved(String playlistCode) {
        PlaylistDto playlistDto = playlistRepository.getByCode(playlistCode);
        List<PlaylistItemSaved> playlistItemSavedList = videoService.getplaylistItemSavedList(playlistDto.getPlaylistId());

        return new PlaylistWithItemListSaved(playlistDto, playlistItemSavedList);
    }

    @Transactional
    public void putPlaylistWithItemListSaved(PlaylistWithItemList playlistWithItemList) {
        putPlaylistSaved(playlistWithItemList, playlistWithItemList.getDuration());
        PlaylistDto playlistDto = playlistRepository.getByCode(playlistWithItemList.getCode());

        playlistVideoRepository.deleteByPlaylistId(playlistDto.getPlaylistId());

        for (PlaylistItem playlistItem : playlistWithItemList.getPlaylistItemList()) {
            videoService.putPlaylistItem(playlistDto.getPlaylistId(), playlistItem);
        }
    }

    private void putPlaylistSaved(Playlist playlist, int playlistDuration) {
        Optional<PlaylistDto> playlistDtoOptional = playlistRepository.findByCode(playlist.getCode());

        if (playlistDtoOptional.isEmpty()) {
            playlistRepository.save(new PlaylistDto(playlist, playlistDuration));
        } else {
            updatePlaylistDto(playlistDtoOptional.get(), playlist, playlistDuration);
        }
    }

    private void updatePlaylistDto(PlaylistDto formerPlaylistDto, Playlist recentPlaylist, int playlistDuration) {
        formerPlaylistDto.setChannel(recentPlaylist.getChannel());
        formerPlaylistDto.setThumbnail(recentPlaylist.getThumbnail());
        formerPlaylistDto.setDescription(recentPlaylist.getDescription());
        formerPlaylistDto.setDuration(playlistDuration);
        formerPlaylistDto.setTitle(recentPlaylist.getTitle());
        formerPlaylistDto.setVideoCount(recentPlaylist.getVideoCount());

        playlistRepository.updateById(formerPlaylistDto.getPlaylistId(), formerPlaylistDto);
    }

    public PlaylistWithItemList getPlaylistWithItemList(String playlistCode) {
        return new PlaylistWithItemList(
                getPlaylist(playlistCode),
                getPlaylistItemList(playlistCode));
    }

    private List<PlaylistItem> getPlaylistItemList(String playlistCode) {
        String nextPageToken = null;
        int pageCount = MAX_PLAYLIST_ITEM / DEFAULT_INDEX_COUNT;

        List<PlaylistItem> playlistItemList = new ArrayList<>();

        for (int i = 0; i < pageCount; i++) {
            PlaylistItemInfo playlistItemInfo = youtubeService.getPlaylistItemInfo(playlistCode, nextPageToken, DEFAULT_INDEX_COUNT);
            pageCount = playlistItemInfo.getTotalResultCount() / DEFAULT_INDEX_COUNT + 1;
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
            if (youtubeService.isPrivacyStatusUnusable(videoInfo)) {
                continue;
            }
            futureList.add(CompletableFuture.supplyAsync(() ->
                    videoService.getPlaylistItem(videoInfo.getVideoCode(), videoInfo.getPosition())
            ));

        }

        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
