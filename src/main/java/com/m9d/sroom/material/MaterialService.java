package com.m9d.sroom.material;

import com.m9d.sroom.common.LearningActivityUpdater;
import com.m9d.sroom.common.entity.CourseQuizEntity;
import com.m9d.sroom.common.entity.CourseVideoEntity;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.course.vo.CourseVideo;
import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.CourseQuizDuplicationException;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.quiz.QuizService;
import com.m9d.sroom.quiz.vo.Quiz;
import com.m9d.sroom.summary.SummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MaterialService {

    private final CourseServiceHelper courseServiceHelper;
    private final SummaryService summaryService;
    private final QuizService quizService;
    private final LearningActivityUpdater activityUpdater;
    private final VideoRepository videoRepository;

    public MaterialService(CourseServiceHelper courseServiceHelper, SummaryService summaryService,
                           QuizService quizService, LearningActivityUpdater activityUpdater,
                           VideoRepository videoRepository) {
        this.courseServiceHelper = courseServiceHelper;
        this.summaryService = summaryService;
        this.quizService = quizService;
        this.activityUpdater = activityUpdater;
        this.videoRepository = videoRepository;
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
            List<QuizResponse> quizResponseList = quizService
                    .getQuizResponseList(courseVideo.getVideoId(), courseVideoId);
            return Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(new SummaryBrief(summaryService.getSummary(courseVideo.getSummaryId())))
                    .quizzes(quizResponseList)
                    .totalQuizCount(quizResponseList.size())
                    .build();
        }
    }


    @Transactional
    public SummaryId updateSummary(Long memberId, Long courseVideoId, String newContent) {
        CourseVideo courseVideo = courseServiceHelper.getCourseVideo(memberId, courseVideoId).toCourseVideo();

        long patchedSummaryId = summaryService.editSummary(courseVideo.getVideoId(), courseVideo.getSummaryId(),
                newContent);
        courseServiceHelper.updateCourseVideoSummaryId(courseVideoId, patchedSummaryId);
        return new SummaryId(patchedSummaryId);
    }

    @Transactional
    public List<SubmittedQuizInfoResponse> submitQuizResults(Long memberId, Long courseVideoId,
                                                             List<SubmittedQuizRequest> submittedQuizList) {
        CourseVideoEntity courseVideoEntity = courseServiceHelper.getCourseVideo(memberId, courseVideoId);
        quizService.validateSubmittedQuizzes(courseVideoEntity.getVideoId(), courseVideoId, submittedQuizList);

        activityUpdater.updateDailyLogQuizCount(memberId, courseVideoEntity.getCourseId(), submittedQuizList.size());
        activityUpdater.updateMemberQuizCount(memberId, submittedQuizList.size(), (int) submittedQuizList.stream()
                .filter(SubmittedQuizRequest::getIsCorrect)
                .count());

        List<SubmittedQuizInfoResponse> quizInfoResponseList = new ArrayList<>();
        for (SubmittedQuizRequest submittedQuizRequest : submittedQuizList) {
            if (quizService.isSubmittedAlready(courseVideoId, submittedQuizRequest.getQuizId())) {
                throw new CourseQuizDuplicationException();
            }

            CourseQuizEntity courseQuizEntity = quizService.createCourseQuizEntity(courseVideoEntity.getCourseId(),
                    courseVideoEntity.getVideoId(), courseVideoId, submittedQuizRequest.getQuizId(),
                    submittedQuizRequest.toVo(), memberId);
            quizInfoResponseList.add(new SubmittedQuizInfoResponse(submittedQuizRequest.getQuizId(),
                    courseQuizEntity.getId()));
        }
        log.info("subject = quizSubmitted, correctAnswerRate = {}",
                ((double) submittedQuizList.stream().filter(SubmittedQuizRequest::getIsCorrect).count()
                        / submittedQuizList.size()));
        return quizInfoResponseList;
    }

    @Transactional
    public ScrapResult switchScrapFlag(Long courseQuizId) {
        return ScrapResult.builder()
                .courseQuizId(courseQuizId)
                .scrapped(quizService.scrap(courseQuizId))
                .build();
    }

    public Material4PdfResponse getCourseMaterials(Long courseId) {
        Course course = courseServiceHelper.getCourse(courseId);

        if (course.hasUnpreparedVideo()) {
            return Material4PdfResponse.createUnprepared(course.getTitle(), course.getCourseVideoList().size());
        }

        List<Content4PdfResponse> contentList = new ArrayList<>();
        List<Answer4PdfResponse> answerList = new ArrayList<>();

        course.getCourseVideoList()
                .forEach(courseVideo -> addMaterialAndQuizAnswer(courseVideo, answerList, contentList));

        return Material4PdfResponse.createPrepared(course.getTitle(), course.getCourseVideoList().size(), contentList,
                answerList);
    }

    private void addMaterialAndQuizAnswer(CourseVideo courseVideo, List<Answer4PdfResponse> answerList,
                                          List<Content4PdfResponse> contentList) {
        int quizIndex = 1;
        SummaryBrief summaryBrief = null;
        List<Quiz4PdfResponse> quizList = new ArrayList<>();

        if (courseVideo.getMaterialStatus().equals(MaterialStatus.CREATED)) {
            summaryBrief = new SummaryBrief(summaryService.getSummary(courseVideo.getSummaryId()));

            for (Quiz quiz : quizService.getQuizList(courseVideo.getVideoId())) {
                quizList.add(new Quiz4PdfResponse(quiz, quizIndex));
                answerList.add(new Answer4PdfResponse(courseVideo.getVideoIndex(), quizIndex, quiz.getAnswer(),
                        quiz.getOptionStrList().get(Integer.parseInt(quiz.getAnswer()) - 1)));
                quizIndex++;
            }
        }

        contentList.add(Content4PdfResponse.create(videoRepository.getById(courseVideo.getVideoId()), courseVideo,
                summaryBrief, quizList));
    }
}
