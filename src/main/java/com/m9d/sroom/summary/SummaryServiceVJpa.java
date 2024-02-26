package com.m9d.sroom.summary;

import com.m9d.sroom.common.entity.jpa.CourseVideoEntity;
import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import com.m9d.sroom.common.repository.summary.SummaryJpaRepository;
import com.m9d.sroom.material.dto.response.SummaryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SummaryServiceVJpa {

    private final SummaryJpaRepository summaryRepository;

    public SummaryServiceVJpa(SummaryJpaRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    @Transactional
    public SummaryId update(CourseVideoEntity courseVideoEntity, String newContent) {
        if (courseVideoEntity.getSummary().isModified()) {
            courseVideoEntity.getSummary().update(newContent);
            log.info("subject = editSummary, modifiedSummary = {} summaryLengthBecameLonger = {}", true,
                    newContent.length() > courseVideoEntity.getSummary().getContent().length());
            return new SummaryId(courseVideoEntity.getSummary().getSummaryId());
        } else {
            SummaryEntity newSummaryEntity = summaryRepository.save(SummaryEntity.create(courseVideoEntity.getVideo(), newContent));
            log.info("subject = editSummary, modifiedSummary = {} summaryLengthBecameLonger = {}", false,
                    newContent.length() > courseVideoEntity.getSummary().getContent().length());
            courseVideoEntity.setSummary(newSummaryEntity);
            return new SummaryId(newSummaryEntity.getSummaryId());
        }
    }
}
