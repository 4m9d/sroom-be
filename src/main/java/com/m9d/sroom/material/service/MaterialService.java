package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotFoundException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
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
import com.m9d.sroom.material.model.Summary;
import com.m9d.sroom.material.repository.MaterialRepository;
import com.m9d.sroom.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public Material getMaterials(Long memberId, Long courseId, Long videoId) {
        Material material;
        List<Quiz> quizList;
        SummaryBrief summaryBrief;

        validateCourseAndVideoForMember(memberId, courseId, videoId);

        Long summaryId = materialRepository.findSummaryIdByCourseAndVideoId(courseId, videoId);

        if (summaryId == null) {
            material = Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else {
            quizList = getQuizList(courseId, videoId);
            summaryBrief = materialRepository.getSummaryById(summaryId);

            material = Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summaryBrief(summaryBrief)
                    .quizzes(quizList)
                    .totalQuizCount(quizList.size())
                    .build();
        }

        return material;
    }

    private List<Quiz> getQuizList(Long courseId, Long videoId) {
        List<Quiz> quizzes = materialRepository.getQuizListByVideoId(videoId);

        for (Quiz quiz : quizzes) {
            List<String> options = materialRepository.getQuizOptionListByQuizId(quiz.getId());
            setOptionsToQuiz(quiz, options);

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

    private void setOptionsToQuiz(Quiz quiz, List<String> options) {
        if (options.size() > 0) quiz.setSelectOption1(options.get(0));
        if (options.size() > 1) quiz.setSelectOption2(options.get(1));
        if (options.size() > 2) quiz.setSelectOption3(options.get(2));
        if (options.size() > 3) quiz.setSelectOption4(options.get(3));
        if (options.size() > 4) quiz.setSelectOption5(options.get(4));
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

        Long courseVideoId = courseRepository.getCourseVideoId(courseId, videoId);
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
