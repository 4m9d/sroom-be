package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotFoundException;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.global.model.CourseVideo;
import com.m9d.sroom.global.model.QuizOption;
import com.m9d.sroom.material.dto.request.SummaryEdit;
import com.m9d.sroom.material.dto.response.SummaryId;
import com.m9d.sroom.material.exception.SummaryNotFoundException;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.material.dto.response.Material;
import com.m9d.sroom.material.dto.response.Quiz;
import com.m9d.sroom.material.dto.response.SummaryBrief;
import com.m9d.sroom.material.exception.CourseIdInvalidParamException;
import com.m9d.sroom.material.model.CourseQuiz;
import com.m9d.sroom.material.model.QuizType;
import com.m9d.sroom.global.model.Summary;
import com.m9d.sroom.material.repository.MaterialRepository;
import com.m9d.sroom.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;

    public MaterialService(MaterialRepository materialRepository, CourseRepository courseRepository) {
        this.materialRepository = materialRepository;
        this.courseRepository = courseRepository;
    }

    public Material getMaterials(Long memberId, Long courseVideoId) {
        Material material;
        List<Quiz> quizList;
        SummaryBrief summaryBrief;

        CourseVideo courseVideo = getCourseVideo(courseVideoId);

        validateCourseVideoIdForMember(memberId, courseVideo);

        if (courseVideo.getSummaryId() == null) {
            material = Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else {
            quizList = getQuizList(courseVideo.getCourseId(), courseVideo.getVideoId());
            summaryBrief = materialRepository.getSummaryById(courseVideo.getSummaryId());

            material = Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(summaryBrief)
                    .quizzes(quizList)
                    .totalQuizCount(quizList.size())
                    .build();
        }

        return material;
    }

    private CourseVideo getCourseVideo(Long courseVideoId) {
        Optional<CourseVideo> courseVideoOpt = courseRepository.findCourseVideoById(courseVideoId);

        if (courseVideoOpt.isEmpty()) {
            throw new CourseVideoNotFoundException();
        }

        return courseVideoOpt.get();
    }

    private void validateCourseVideoIdForMember(Long memberId, CourseVideo courseVideo) {
        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }
    }

    private List<Quiz> getQuizList(Long courseId, Long videoId) {
        List<Quiz> quizzes = materialRepository.getQuizListByVideoId(videoId);

        for (Quiz quiz : quizzes) {
            List<QuizOption> options = materialRepository.getQuizOptionListByQuizId(quiz.getId());
            setQuizOptions(quiz, options);

            Optional<CourseQuiz> courseQuizOpt = materialRepository.findCourseQuizInfo(quiz.getId(), videoId, courseId);
            if (courseQuizOpt.isPresent()) {
                CourseQuiz courseQuiz = courseQuizOpt.get();
                quiz.setSubmitted(true);
                quiz.setSubmittedAnswer(courseQuiz.getSubmittedAnswer());
                quiz.setCorrect(courseQuiz.isCorrect());
                quiz.setSubmittedAt(DateUtil.dateFormat.format(courseQuiz.getSubmittedTime()));

            } else {
                quiz.setSubmitted(false);
            }
            translateNumToTF(quiz, courseQuizOpt);
        }

        return quizzes;
    }

    private void setQuizOptions(Quiz quiz, List<QuizOption> options) {
        List<String> optionList = new ArrayList<>(5);
        for (QuizOption option : options) {
            optionList.add(option.getIndex(), option.getOptionText());
        }
        quiz.setOptions(optionList);
    }

    private void translateNumToTF(Quiz quiz, Optional<CourseQuiz> courseQuizOpt) {
        if (quiz.getType() != QuizType.TRUE_FALSE.getValue()) {
            return;
        }

        if (courseQuizOpt.isPresent()) {
            String submittedAnswer = courseQuizOpt.get().getSubmittedAnswer().equals("0") ? "false" : "true";
            quiz.setSubmittedAnswer(submittedAnswer);
        }

        String answer = quiz.getAnswer().equals("0") ? "false" : "true";
        quiz.setAnswer(answer);
    }

    public SummaryId updateSummary(Long memberId, Long videoId, SummaryEdit summaryEdit) {
        long courseId = summaryEdit.getCourse_id();
        String newContent = summaryEdit.getContent();

        validateCourseAndVideoForMember(memberId, courseId, videoId);

        Optional<Summary> originalSummaryOpt = materialRepository.findSummaryByCourseVideo(courseId, videoId);
        if (originalSummaryOpt.isEmpty()) {
            throw new SummaryNotFoundException();
        }

        Summary originalSummary = originalSummaryOpt.get();
        long summaryId;

        if (originalSummary.isModified()) {
            summaryId = originalSummary.getId();
            materialRepository.updateSummary(summaryId, newContent);
        } else {
            summaryId = materialRepository.saveSummaryModified(videoId, newContent);
            materialRepository.updateSummaryIdByCourseVideo(videoId, courseId, summaryId);
        }

        return SummaryId.builder()
                .summaryId(summaryId)
                .build();
    }

    private void validateCourseAndVideoForMember(Long memberId, Long courseId, Long videoId) {
        validateCourseIdNull(courseId);

        Long originMemberId = courseRepository.getMemberIdByCourseId(courseId);
        if (!originMemberId.equals(memberId)) {
            throw new CourseNotFoundException();
        }

        Long courseVideoId = courseRepository.findCourseVideoId(courseId, videoId);
        if (courseVideoId == null) {
            throw new CourseVideoNotFoundException();
        }
    }

    private void validateCourseIdNull(Long courseId) {
        if (courseId == null) {
            throw new CourseIdInvalidParamException();
        }
    }
}
