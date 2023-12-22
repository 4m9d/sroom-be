package com.m9d.sroom.video;

import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import com.m9d.sroom.util.constant.ContentConstant;
import com.m9d.sroom.video.vo.Video;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class VideoRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("영상 저장에 성공합니다.")
    void saveVideo() {
        //given
        Video videoVo = Video.builder()
                .code(ContentConstant.VIDEO_CODE_LIST[0])
                .title(ContentConstant.VIDEO_TITLE)
                .channel(TestConstant.PLAYLIST_CHANNEL)
                .thumbnail(TestConstant.THUMBNAIL)
                .description(TestConstant.PLAYLIST_DESCRIPTION)
                .duration(100)
                .viewCount(10000L)
                .publishedAt(new Timestamp(System.currentTimeMillis()))
                .language("ko")
                .license("youtube")
                .membership(false)
                .reviewCount(0)
                .rating(0.0)
                .build();

        //when
        VideoEntity videoEntity = videoRepository.save(VideoEntity.create(videoVo));

        //then
        Assertions.assertEquals(1L, (long) videoEntity.getVideoId());
        Assertions.assertEquals(videoEntity.getContentInfo().getDescription(), TestConstant.PLAYLIST_DESCRIPTION);
    }
}
