package com.m9d.sroom.video;

import com.m9d.sroom.ai.AiService;
import com.m9d.sroom.common.entity.PlaylistVideoEntity;
import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.common.repository.playlistvideo.PlaylistVideoRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.video.vo.PlaylistItem;
import com.m9d.sroom.video.vo.Video;
import com.m9d.sroom.course.dto.EnrollContentInfo;
import com.m9d.sroom.course.dto.InnerContent;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.video.constant.VideoConstant;
import com.m9d.sroom.youtube.YoutubeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final YoutubeMapper youtubeService;
    private final PlaylistVideoRepository playlistVideoRepository;
    private final AiService aiService;

    public VideoService(VideoRepository videoRepository, YoutubeMapper youtubeService,
                        PlaylistVideoRepository playlistVideoRepository, AiService aiService) {
        this.videoRepository = videoRepository;
        this.youtubeService = youtubeService;
        this.playlistVideoRepository = playlistVideoRepository;
        this.aiService = aiService;
    }


    public Video getRecentVideo(String videoCode) {
        Optional<VideoEntity> videoEntityOptional = videoRepository.findByCode(videoCode);

        if (videoEntityOptional.isPresent()
                && DateUtil.hasRecentUpdate(videoEntityOptional.get().getUpdatedAt(), VideoConstant.VIDEO_UPDATE_THRESHOLD_HOURS)) {
            return videoEntityOptional.get().toVideo();
        } else {
            return youtubeService.getVideo(videoCode);
        }
    }

    public void putVideo(Video video) {
        Optional<VideoEntity> videoEntityOptional = videoRepository.findByCode(video.getCode());

        VideoEntity videoEntity;
        if (videoEntityOptional.isEmpty()) {
            videoEntity = videoRepository.save(new VideoEntity(video, (long) MaterialStatus.CREATING.getValue()));
        } else if (!DateUtil.hasRecentUpdate(videoEntityOptional.get().getUpdatedAt(), VideoConstant.VIDEO_UPDATE_THRESHOLD_HOURS)) {
            videoEntity = videoRepository.updateById(videoEntityOptional.get().getVideoId(), videoEntityOptional.get()
                    .updateByYoutube(video, videoEntityOptional.get().getSummaryId()));
        } else {
            videoEntity = videoEntityOptional.get();
        }

        if (videoEntity.getMaterialStatus() == null
                || videoEntity.getMaterialStatus() == MaterialStatus.NO_REQUEST.getValue()) {
            log.info("request to AI server successfully. videoCode = {}, title = {}", video.getCode(), video.getTitle());
            aiService.requestToFastApi(video.getCode(), video.getTitle());
            videoEntity.setMaterialStatus(MaterialStatus.CREATING.getValue());
            videoRepository.updateById(videoEntity.getVideoId(), videoEntity);
        }
    }

    public void putPlaylistItem(Long playlistId, PlaylistItem playlistItem) {
        putVideo(playlistItem);
        VideoEntity videoEntity = videoRepository.getByCode(playlistItem.getCode());

        playlistVideoRepository.save(PlaylistVideoEntity.builder()
                .playlistId(playlistId)
                .videoId(videoEntity.getVideoId())
                .videoIndex(playlistItem.getIndex())
                .build());
    }

    public EnrollContentInfo getEnrollContentInfo(String videoCode) {
        VideoEntity videoEntity = videoRepository.getByCode(videoCode);

        return new EnrollContentInfo(false, videoEntity.getVideoId(), videoEntity.getTitle(),
                videoEntity.getDuration(), videoEntity.getThumbnail(), videoEntity.getChannel(),
                new ArrayList<>(List.of(
                        new InnerContent(videoEntity.getVideoId(), videoEntity.getSummaryId(), videoEntity.getDuration())
                )));
    }

    public List<InnerContent> getEnrollInnerContentList(Long playlistId) {
        List<VideoEntity> videoEntityList = videoRepository.getListByPlaylistId(playlistId);

        List<InnerContent> innerContentList = new ArrayList<>();
        for (VideoEntity video : videoEntityList) {
            innerContentList.add(new InnerContent(video.getVideoId(), video.getSummaryId(), video.getDuration()));
        }
        return innerContentList;
    }

    public Collection<String> getEnrolledCodeSet(Long memberId) {
        return videoRepository.getCodeSetByMemberId(memberId);
    }

    public List<VideoEntity> getTopRatedVideos(int limit) {
        return videoRepository.getTopRatedOrder(limit);
    } // video service 로 이동
}
