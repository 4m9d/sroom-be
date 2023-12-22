package com.m9d.sroom.summary;

import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.common.repository.summary.SummaryJpaRepository;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SummaryRepositoryTest extends RepositoryTest {

    @Autowired
    SummaryJpaRepository summaryRepository;

    @Test
    @DisplayName("강의노트를 저장합니다.")
    void saveSummary() {
        //given
        VideoEntity video = getVideoEntity(ContentConstant.VIDEO_CODE_LIST[0]);

        //when
        summaryRepository.save(SummaryEntity.create(video, "SUMMARY"));

        //then
        Assertions.assertEquals(summaryRepository.getById(1L).getContent(), "SUMMARY");
    }
}
