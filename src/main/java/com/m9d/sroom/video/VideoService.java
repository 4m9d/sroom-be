package com.m9d.sroom.video;

import com.m9d.sroom.common.dto.PlaylistVideoDto;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.video.repository.VideoRepository;
import com.m9d.sroom.youtube.YoutubeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private final YoutubeService youtubeService;
    private final VideoRepository videoRepository;
    private final PlaylistVideoRepository playlistVideoRepository;

    public VideoService(YoutubeService youtubeService, VideoRepository videoRepository, PlaylistVideoRepository playlistVideoRepository) {
        this.youtubeService = youtubeService;
        this.videoRepository = videoRepository;
        this.playlistVideoRepository = playlistVideoRepository;
    }

    public Video getVideo(String videoCode) {  // 최신의 video 정보를 반환합니다.
        Optional<VideoDto> videoDtoOptional = videoRepository.findByCode(videoCode);

        if (videoDtoOptional.isEmpty() || !new VideoSaved(videoDtoOptional.get()).isRecentContent()) {
            return new Video(youtubeService.getVideoInfo(videoCode));
        } else {
            return new Video(videoDtoOptional.get().toVideoInfo());
        }
    }

    public PlaylistItem getPlaylistItem(String videoCode, Integer position) {
        return getVideo(videoCode).toPlaylistItem(position);
    }

    public VideoSaved getVideoSaved(String videoCode) {
        return new VideoSaved(videoRepository.getByCode(videoCode));
    }

    public List<PlaylistItemSaved> getplaylistItemSavedList(Long playlistId) {
        List<PlaylistItemSaved> playlistItemSavedList = new ArrayList<>();

        for (VideoDto videoDto : videoRepository.getListByPlaylistId(playlistId)) {
            playlistItemSavedList.add(new PlaylistItemSaved(videoDto, videoDto.getIndex()));
        }
        return playlistItemSavedList;
    }

    @Transactional
    public void putVideoSaved(Video video) {
        Optional<VideoDto> videoDtoOptional = videoRepository.findByCode(video.getCode());

        if (videoDtoOptional.isEmpty()) {
            videoRepository.save(new VideoDto(video));
        } else {
            updateVideoDto(videoDtoOptional.get(), video);
        }
    }

    @Transactional
    public void putPlaylistItem(Long playlistId, PlaylistItem playlistItem) {
        putVideoSaved(playlistItem);
        VideoDto videoDto = videoRepository.getByCode(playlistItem.getCode());

        playlistVideoRepository.save(PlaylistVideoDto.builder()
                .playlistId(playlistId)
                .videoId(videoDto.getVideoId())
                .videoIndex(playlistItem.getIndex())
                .build());
    }

    private void updateVideoDto(VideoDto formerVideoDto, Video recentVideo) {
        formerVideoDto.setDuration(recentVideo.getDuration());
        formerVideoDto.setChannel(recentVideo.getChannel());
        formerVideoDto.setThumbnail(recentVideo.getThumbnail());
        formerVideoDto.setDescription(recentVideo.getDescription());
        formerVideoDto.setTitle(recentVideo.getTitle());
        formerVideoDto.setLanguage((recentVideo.getLanguage()));
        formerVideoDto.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        formerVideoDto.setViewCount(recentVideo.getViewCount());
        formerVideoDto.setMembership(recentVideo.getMembership());

        videoRepository.updateById(formerVideoDto.getVideoId(), formerVideoDto);
    }
}
