package com.m9d.sroom.video;

import com.m9d.sroom.ai.AiService;
import com.m9d.sroom.common.entity.jpa.PlaylistEntity;
import com.m9d.sroom.common.entity.jpa.PlaylistVideoEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoJpaRepository;
import com.m9d.sroom.common.repository.video.VideoJpaRepository;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.video.constant.VideoConstant;
import com.m9d.sroom.video.vo.PlaylistItem;
import com.m9d.sroom.video.vo.Video;
import com.m9d.sroom.youtube.YoutubeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VideoServiceVJpa {

    private final VideoJpaRepository videoRepository;
    private final PlaylistVideoJpaRepository playlistVideoRepository;
    private final YoutubeMapper youtubeService;
    private final AiService aiService;

    public VideoServiceVJpa(VideoJpaRepository videoRepository, PlaylistVideoJpaRepository playlistVideoRepository, YoutubeMapper youtubeMapper, AiService aiService) {
        this.videoRepository = videoRepository;
        this.playlistVideoRepository = playlistVideoRepository;
        this.youtubeService = youtubeMapper;
        this.aiService = aiService;
    }

    public Video getRecent(String videoCode) {
        Optional<VideoEntity> videoEntityOptional = videoRepository.findByCode(videoCode);

        Video video;

        if (videoEntityOptional.isPresent()) {
            if (DateUtil.hasRecentUpdate(videoEntityOptional.get().getUpdatedAt(),
                    VideoConstant.VIDEO_UPDATE_THRESHOLD_HOURS)) {
                video = VideoMapper.getVoByEntity(videoEntityOptional.get());
            } else {
                video = youtubeService.getVideo(videoCode);
                video.setReviewInfo(videoEntityOptional.get().getReview());
            }
        } else {
            video = youtubeService.getVideo(videoCode);
        }

        return video;
    }

    public void put(Video video) {
        Optional<VideoEntity> videoEntityOptional = videoRepository.findByCode(video.getCode());

        VideoEntity videoEntity;
        if (videoEntityOptional.isEmpty()) {
            videoEntity = videoRepository.save(VideoEntity.create(video));
        } else if (DateUtil.hasRecentUpdate(videoEntityOptional.get().getUpdatedAt(),
                VideoConstant.VIDEO_UPDATE_THRESHOLD_HOURS)) {
            videoEntity = videoEntityOptional.get();
        } else {
            videoEntity = videoEntityOptional.get();
            videoEntityOptional.get().update(video);
        }

        if (videoEntity.getMaterialStatus() == null ||
                videoEntity.getMaterialStatus() == MaterialStatus.NO_REQUEST.getValue()) {
            log.info("subject = requestToAIServer, videoCode = {}, title = {}", video.getCode(), video.getTitle());
            aiService.requestToFastApi(video.getCode(), video.getTitle());
            videoEntity.setMaterialStatus(MaterialStatus.CREATING);
        }
    }

    public void putItem(PlaylistEntity playlistEntity, PlaylistItem playlistItem) {
        put(playlistItem);
        VideoEntity videoEntity = videoRepository.getByCode(playlistItem.getCode());

        playlistVideoRepository.save(PlaylistVideoEntity.create(playlistEntity, videoEntity, playlistItem.getIndex()));
    }
}
