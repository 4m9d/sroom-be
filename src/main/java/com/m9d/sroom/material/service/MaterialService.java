package com.m9d.sroom.material.service;

import com.m9d.sroom.course.exception.CourseNotFoundException;
import com.m9d.sroom.course.exception.CourseVideoNotFoundException;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.material.dto.response.Material;
import com.m9d.sroom.material.dto.response.Quiz;
import com.m9d.sroom.material.dto.response.Summary;
import com.m9d.sroom.material.exception.CourseIdInvalidParamException;
import com.m9d.sroom.material.model.CourseQuiz;
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
        Summary summary;

        validateCourseAndVideoForMember(memberId, courseId, videoId);

        Long summaryId = materialRepository.findSummaryIdFromCourseVideo(courseId, videoId);

        if (summaryId == null) {
            material = Material.builder()
                    .status(MaterialStatus.CREATING.getValue())
                    .build();
        } else {
            quizList = getQuizList(courseId, videoId);
            summary = materialRepository.getSummaryById(summaryId);

            material = Material.builder()
                    .status(MaterialStatus.CREATED.getValue())
                    .summary(summary)
                    .quizzes(quizList)
                    .totalQuizCount(quizList.size())
                    .build();
        }

        return material;
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

                translateNumToTF(quiz, courseQuiz);
            } else {
                quiz.setSubmitted(false);
            }
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

    private void translateNumToTF(Quiz quiz, CourseQuiz courseQuiz) {
        if(quiz.getType() == 3){
            String answer = courseQuiz.getSubmittedAnswer().equals("0") ? "false" : "true";
            quiz.setSubmittedAnswer(answer);
        }
    }
}
