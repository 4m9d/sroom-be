package com.m9d.sroom.lecture;

import com.m9d.sroom.common.entity.PlaylistEntity;
import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.recommendation.dto.RecommendLecture;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LectureResponseServiceTest extends ServiceTest {

    @Test
    @DisplayName("평점이 높은 순으로 비디오를 불러옵니다.")
    void getTopRatedVideoTest() {
        //given
        saveVideo("code1", 100, "channel1", "thumbnail url", 0, 0, "title1", " ", 1500);
        saveVideo("code2", 321, "channel1", "thumbnail url", 1470, 300, "title2", " ", 300);
        saveVideo("code3", 12, "channel2", "thumbnail url", 900, 200, "title3", " ", 300000);

        //when
        List<VideoEntity> topRatedVideos = videoService.getTopRatedVideos(3);

        //then
        Assertions.assertEquals("title2", topRatedVideos.get(0).getTitle());
        Assertions.assertEquals("title3", topRatedVideos.get(1).getTitle());
        Assertions.assertEquals("title1", topRatedVideos.get(2).getTitle());
    }

    @Test
    @DisplayName("평점이 높은 순으로 플레이리스트를 불러옵니다.")
    void getTopRatedPlaylistTest() {
        //given
        savePlaylist("code1", 100, "channel1", "thumbnail url", 100, 150, "title1");
        savePlaylist("code2", 100, "channel1", "thumbnail url", 700, 150, "title2");
        savePlaylist("code3", 100, "channel1", "thumbnail url", 600, 150, "title3");

        //when
        List<PlaylistEntity> topRatedPlaylists = playlistService.getTopRatedPlaylists(3);

        //then
        Assertions.assertEquals("title2", topRatedPlaylists.get(0).getTitle());
        Assertions.assertEquals("title3", topRatedPlaylists.get(1).getTitle());
        Assertions.assertEquals("title1", topRatedPlaylists.get(2).getTitle());
    }

    @Test
    @DisplayName("유저가 가장 많이 등록한 강의 채널리스트를 불러옵니다.")
    void getMostEnrolledChannelTest() {
        //given
        MemberEntity member = getNewMember();
        Long memberId = member.getMemberId();

        courseRepository.saveLecture(memberId, 1L, 1L, "channel1", false, 1);
        courseRepository.saveLecture(memberId, 1L, 2L, "channel1", false, 2);
        courseRepository.saveLecture(memberId, 1L, 3L, "channel1", false, 3);

        courseRepository.saveLecture(memberId, 2L, 4L, "channel2", false, 1);
        courseRepository.saveLecture(memberId, 2L, 5L, "channel2", false, 2);

        courseRepository.saveLecture(memberId, 3L, 6L, "channel3", true, 1);

        //when
        List<String> mostEnrolledChannels = lectureService.getMostEnrolledChannels(memberId);

        //then
        Assertions.assertEquals("channel1", mostEnrolledChannels.get(0));
        Assertions.assertEquals("channel2", mostEnrolledChannels.get(1));
        Assertions.assertEquals("channel3", mostEnrolledChannels.get(2));
    }
}