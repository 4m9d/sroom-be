package com.m9d.sroom.quiz;

import com.m9d.sroom.common.LearningActivityUpdaterVJpa;
import com.m9d.sroom.common.entity.jpa.CourseQuizEntity;
import com.m9d.sroom.common.entity.jpa.CourseVideoEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.QuizEntity;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizJpaRepository;
import com.m9d.sroom.common.repository.quiz.QuizJpaRepository;
import com.m9d.sroom.course.exception.CourseNotMatchException;
import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.response.ScrapResult;
import com.m9d.sroom.material.dto.response.SubmittedQuizInfoResponse;
import com.m9d.sroom.material.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuizServiceVJpa {

    private final QuizJpaRepository quizRepository;
    private final CourseQuizJpaRepository courseQuizRepository;
    private final LearningActivityUpdaterVJpa learningActivityUpdater;

    public QuizServiceVJpa(QuizJpaRepository quizRepository, CourseQuizJpaRepository courseQuizRepository,
                           LearningActivityUpdaterVJpa learningActivityUpdater) {
        this.quizRepository = quizRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.learningActivityUpdater = learningActivityUpdater;
    }

    @Transactional
    public List<SubmittedQuizInfoResponse> submit(CourseVideoEntity courseVideoEntity,
                                                  List<SubmittedQuizRequest> submittedQuizList) {
        validateSubmitted(courseVideoEntity, submittedQuizList);

        learningActivityUpdater.updateDailyQuizCount(courseVideoEntity.getCourse(), submittedQuizList.size());
        courseVideoEntity.getMember().addQuizCount(submittedQuizList.size());
        courseVideoEntity.getMember().addCorrectQuizCount((int) submittedQuizList.stream()
                .filter(SubmittedQuizRequest::getIsCorrect)
                .count());

        List<SubmittedQuizInfoResponse> quizInfoResponseList = new ArrayList<>();
        for (SubmittedQuizRequest submittedQuizRequest : submittedQuizList) {
            Optional<CourseQuizEntity> courseQuizEntityOptional = courseVideoEntity
                    .findCourseQuizByQuizId(submittedQuizRequest.getId());

            if (courseQuizEntityOptional.isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            QuizEntity quizEntity = quizRepository.getById(submittedQuizRequest.getId());

            CourseQuizEntity courseQuizEntity = courseQuizRepository.save(CourseQuizEntity.create(
                    courseVideoEntity, quizEntity, submittedQuizRequest.getSubmittedAnswer(),
                    submittedQuizRequest.getIsCorrect()));

            quizInfoResponseList.add(new SubmittedQuizInfoResponse(quizEntity.getQuizId(),
                    courseQuizEntity.getCourseQuizId()));
        }

        log.info("subject = quizSubmitted, correctAnswerRate = {}",
                ((double) submittedQuizList.stream().filter(SubmittedQuizRequest::getIsCorrect).count()
                        / submittedQuizList.size()));
        return quizInfoResponseList;
    }

    private void validateSubmitted(CourseVideoEntity courseVideoEntity, List<SubmittedQuizRequest> submittedQuizList) {
        QuizEntity quizEntity = quizRepository.findById(submittedQuizList.get(0).getId())
                .orElseThrow(QuizNotFoundException::new);

        if (!quizEntity.getVideo().equals(courseVideoEntity.getVideo())) {
            throw new QuizNotMatchException();
        }

        for (SubmittedQuizRequest submittedQuiz : submittedQuizList) {
            if (courseVideoEntity.findCourseQuizByQuizId(submittedQuiz.getId())
                    .isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            if (submittedQuiz.getIsCorrect() == null) {
                throw new QuizAnswerFormatNotValidException();
            }
        }
    }

    @Transactional
    public ScrapResult switchScrapFlag(MemberEntity memberEntity, Long courseQuizId) {
        CourseQuizEntity courseQuizEntity = courseQuizRepository.findById(courseQuizId)
                .orElseThrow(CourseQuizNotFoundException::new);
        if (!courseQuizEntity.getMember().equals(memberEntity)) {
            throw new CourseNotMatchException();
        }

        return ScrapResult.builder()
                .courseQuizId(courseQuizEntity.getCourseQuizId())
                .scrapped(courseQuizEntity.scrap())
                .build();
    }


}
