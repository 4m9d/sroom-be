package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.global.model.CourseVideo;
import com.m9d.sroom.global.model.QuizOption;
import com.m9d.sroom.material.dto.response.SummaryId;
import com.m9d.sroom.material.exception.SummaryNotFoundException;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.material.dto.response.Material;
import com.m9d.sroom.material.dto.response.Quiz;
import com.m9d.sroom.material.dto.response.SummaryBrief;
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

    public SummaryId updateSummary(Long memberId, Long courseVideoId, String content) {
        validateCourseVideoForMember(memberId, courseVideoId);

        Summary originalSummary = getSummary(courseVideoId);
        long summaryId;

        if (originalSummary.isModified()) {
            summaryId = originalSummary.getId();
            materialRepository.updateSummary(summaryId, content);
        } else {
            summaryId = materialRepository.saveSummaryModified(originalSummary.getVideoId(), content);
            materialRepository.updateSummaryIdByCourseVideoId(courseVideoId, summaryId);
        }

        return SummaryId.builder()
                .summaryId(summaryId)
                .build();
    }

    private void validateCourseVideoForMember(Long memberId, Long courseVideoId) {
        Optional<CourseVideo> courseVideoOptional = courseRepository.findCourseVideoById(courseVideoId);
        if (courseVideoOptional.isEmpty()) {
            throw new CourseVideoNotFoundException();
        }

        CourseVideo courseVideo = courseVideoOptional.get();
        if (!courseVideo.getMemberId().equals(memberId)) {
            throw new CourseNotMatchException();
        }
    }

    private Summary getSummary(Long courseVideoId) {
        Optional<Summary> originalSummaryOpt = materialRepository.findSummaryByCourseVideoId(courseVideoId);
        if (originalSummaryOpt.isEmpty()) {
            throw new SummaryNotFoundException();
        }

        Summary originalSummary = originalSummaryOpt.get();
        return originalSummary;
    }
}
