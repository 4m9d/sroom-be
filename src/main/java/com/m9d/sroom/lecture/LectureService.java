package com.m9d.sroom.lecture;

import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.common.repository.playlist.PlaylistRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class LectureService {

    private final VideoRepository videoRepository;
    private final PlaylistRepository playlistRepository;
    private final LectureRepository lectureRepository;

    public LectureService(VideoRepository videoRepository, PlaylistRepository playlistRepository,
                          LectureRepository lectureRepository) {
        this.videoRepository = videoRepository;
        this.playlistRepository = playlistRepository;
        this.lectureRepository = lectureRepository;
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
}
