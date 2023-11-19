package com.m9d.sroom.summary;

import com.m9d.sroom.common.entity.SummaryEntity;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.summary.SummaryRepository;
import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.material.dto.response.FeedbackInfo;
import com.m9d.sroom.material.exception.SummaryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final CourseServiceHelper courseServiceHelper;
    private final CourseVideoRepository courseVideoRepository;

    public SummaryService(SummaryRepository summaryRepository, CourseServiceHelper courseServiceHelper, CourseVideoRepository courseVideoRepository) {
        this.summaryRepository = summaryRepository;
        this.courseServiceHelper = courseServiceHelper;
        this.courseVideoRepository = courseVideoRepository;
    }

    @Transactional
    public long editSummary(Long videoId, Long originalSummaryId, String content) {
        SummaryEntity summaryEntity = summaryRepository.findById(originalSummaryId)
                .orElseThrow(SummaryNotFoundException::new);

        if (summaryEntity.isModified()) {
            updateContent(content, summaryEntity);
            log.info("subject = editSummary, modifiedSummary = {} summaryLengthBecameLonger = {}", true,
                    content.length() > summaryEntity.getContent().length());
        } else {
            summaryEntity = summaryRepository.save(new SummaryEntity(videoId, content, true));
            log.info("subject = editSummary, modifiedSummary = {} summaryLengthBecameLonger = {}", false,
                    content.length() > summaryEntity.getContent().length());
        }

        return summaryEntity.getId();
    }

    private void updateContent(String content, SummaryEntity summaryEntity) {
        summaryEntity.setContent(content);
        summaryRepository.updateById(summaryEntity.getId(), summaryEntity);
    }

    public Summary getSummary(Long summaryId) {
        return summaryRepository.getById(summaryId).toSummary();
    }

    public SummaryEntity getSummaryEntity(Long summaryId){
        return summaryRepository.getById(summaryId);
    }
}
