package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.QuizEntity;
import com.m9d.sroom.common.repository.summary.SummaryRepository;
import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.course.CourseVideo;
import com.m9d.sroom.material.dto.response.Material;
import com.m9d.sroom.material.dto.response.QuizRes;
import com.m9d.sroom.material.dto.response.SummaryBrief;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.quiz.QuizService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialServiceV2 {

    private final CourseServiceHelper courseServiceHelper;
    private final SummaryRepository summaryRepository;
    private final QuizService quizService;

    public MaterialServiceV2(CourseServiceHelper courseServiceHelper, SummaryRepository summaryRepository, QuizService quizService) {
        this.courseServiceHelper = courseServiceHelper;
        this.summaryRepository = summaryRepository;
        this.quizService = quizService;
    }


    public Material getMaterials(Long memberId, Long courseVideoId) {
        CourseVideo courseVideo = courseServiceHelper.getCourseVideo(memberId, courseVideoId).toCourseVideo();

        MaterialStatus materialStatus = courseVideo.getMaterialStatus();

        if (materialStatus.equals(MaterialStatus.CREATING)) {
            return Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else if (materialStatus.equals(MaterialStatus.CREATION_FAILED)) {
            return Material.builder()
                    .status(MaterialStatus.CREATION_FAILED.getValue())
                    .build();
        } else {
            List<QuizRes> quizResList = quizService.getQuizResList(courseVideo.getVideoId(), courseVideoId);
            return Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(new SummaryBrief(summaryRepository.getById(courseVideo.getSummaryId())))
                    .quizzes(quizResList)
                    .totalQuizCount(quizResList.size())
                    .build();
        }
    }



}
