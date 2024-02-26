package com.m9d.sroom.playlist;

import com.m9d.sroom.common.entity.jpa.PlaylistEntity;
import com.m9d.sroom.common.entity.jpa.PlaylistVideoEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.playlist.vo.Playlist;
import com.m9d.sroom.playlist.vo.PlaylistWithItemList;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import com.m9d.sroom.util.constant.ContentConstant;
import com.m9d.sroom.video.vo.PlaylistItem;
import com.m9d.sroom.video.vo.Video;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

public class PlaylistRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("플레이리스트를 저장합니다.")
    void savePlaylist() {
        //given
        Video video1 = Video.builder().code(ContentConstant.VIDEO_CODE_LIST[0]).title(ContentConstant.VIDEO_TITLE)
                .channel(TestConstant.PLAYLIST_CHANNEL).thumbnail(TestConstant.THUMBNAIL)
                .description(TestConstant.PLAYLIST_DESCRIPTION).duration(100).viewCount(10000L)
                .publishedAt(new Timestamp(System.currentTimeMillis())).language("ko").license("youtube")
                .membership(false).reviewCount(0).rating(0.0).build();
        Video video2 = Video.builder().code(ContentConstant.VIDEO_CODE_LIST[1]).title(ContentConstant.VIDEO_TITLE)
                .channel(TestConstant.PLAYLIST_CHANNEL).thumbnail(TestConstant.THUMBNAIL)
                .description(TestConstant.PLAYLIST_DESCRIPTION).duration(100).viewCount(10000L)
                .publishedAt(new Timestamp(System.currentTimeMillis())).language("ko").license("youtube")
                .membership(false).reviewCount(0).rating(0.0).build();

        VideoEntity videoEntity1 = videoRepository.save(VideoEntity.create(video1));
        VideoEntity videoEntity2 = videoRepository.save(VideoEntity.create(video2));

        PlaylistItem playlistItem1 = new PlaylistItem(video1, 1);
        PlaylistItem playlistItem2 = new PlaylistItem(video2, 2);
        List<PlaylistItem> playlistItemList = List.of(playlistItem1, playlistItem2);

        //when
        Playlist playlist = new Playlist(ContentConstant.PLAYLIST_CODE, "플리제목", "채널명", "썸네일",
                "설명쓰", new Timestamp(System.currentTimeMillis() - 10000), 3, 0,
                0.0);
        PlaylistWithItemList playlistWithItemList = new PlaylistWithItemList(playlist, playlistItemList);

        PlaylistEntity playlistEntity = playlistRepository.save(PlaylistEntity.create(playlistWithItemList));
        List<PlaylistVideoEntity> playlistVideoEntityList =
                playlistEntity.createPlaylistVideo(List.of(videoEntity1, videoEntity2));

        for (PlaylistVideoEntity playlistVideoEntity : playlistVideoEntityList) {
            playlistVideoRepository.save(playlistVideoEntity);
        }

        //then
        Assertions.assertEquals(playlistRepository.getByCode(ContentConstant.PLAYLIST_CODE), playlistEntity);
        Assertions.assertEquals(playlistEntity.getVideoList().size(), 2);
    }
}
