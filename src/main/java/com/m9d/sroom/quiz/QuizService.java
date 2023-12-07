package com.m9d.sroom.quiz;

import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import com.m9d.sroom.common.entity.CourseQuizEntity;
import com.m9d.sroom.common.entity.QuizEntity;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.material.FeedbackService;
import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.response.QuizResponse;
import com.m9d.sroom.material.exception.CourseQuizDuplicationException;
import com.m9d.sroom.material.exception.QuizAnswerFormatNotValidException;
import com.m9d.sroom.material.exception.QuizIdNotMatchException;
import com.m9d.sroom.material.exception.QuizNotFoundException;
import com.m9d.sroom.material.model.MaterialType;
import com.m9d.sroom.quiz.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final FeedbackService feedbackService;

    public QuizService(QuizRepository quizRepository, CourseQuizRepository courseQuizRepository,
                       QuizOptionRepository quizOptionRepository, FeedbackService feedbackService) {
        this.quizRepository = quizRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.quizOptionRepository = quizOptionRepository;
        this.feedbackService = feedbackService;
    }

    public List<QuizResponse> getQuizResponseList(Long memberId, Long videoId, Long courseVideoId) {
        List<QuizResponse> quizResponseList = new ArrayList<>();

        for (QuizEntity quizEntity : quizRepository.getListByVideoId(videoId)) {
            quizResponseList.add(new QuizResponse(quizEntity.getId(), getQuiz(quizEntity),
                    getSubmittedInfo(quizEntity.getId(), courseVideoId),
                    feedbackService.getFeedbackInfo(memberId, MaterialType.QUIZ, quizEntity.getId())));
        }
        return quizResponseList;
    }

    public List<Quiz> getQuizList(Long videoId) {
        return quizRepository.getListByVideoId(videoId).stream()
                .map(this::getQuiz)
                .collect(Collectors.toList());
    }

    public Quiz getQuiz(QuizEntity quizEntity) {
        switch (QuizType.fromValue(quizEntity.getType())) {
            case MULTIPLE_CHOICE:
                return new MultipleChoice(quizEntity.getQuestion(), getQuizOptionList(quizEntity.getId(),
                        quizEntity.getChoiceAnswer()), quizEntity.getChoiceAnswer());
            case SUBJECTIVE:
                return new ShortAnswerQuestion(quizEntity.getQuestion(), quizEntity.getSubjectiveAnswer());
            case TRUE_FALSE:
                return new TFQuestion(quizEntity.getQuestion(), quizEntity.getChoiceAnswer(),
                        getQuizOptionList(quizEntity.getId(), quizEntity.getChoiceAnswer()));
            default:
                throw new QuizTypeNotMatchException(quizEntity.getType());
        }
    }

    public List<QuizOption> getQuizOptionList(Long quizId, int answer) {
        return quizOptionRepository.getListByQuizId(quizId).stream()
                .map(optionEntity -> optionEntity.toQuizOption(answer))
                .collect(Collectors.toList());
    }

    private QuizSubmittedInfo getSubmittedInfo(Long quizId, Long courseVideoId) {
        Optional<CourseQuizEntity> courseQuizEntityOptional = courseQuizRepository
                .findByQuizIdAndCourseVideoId(quizId, courseVideoId);

        return courseQuizEntityOptional.map(QuizSubmittedInfo::new)
                .orElseGet(QuizSubmittedInfo::createNotSubmittedInfo);
    }

    public void validateSubmittedQuizzes(Long videoId, Long courseVideoId,
                                         List<SubmittedQuizRequest> submittedQuizRequestList) {
        QuizEntity quiz = quizRepository.findById(submittedQuizRequestList.get(0).getId())
                .orElseThrow(QuizNotFoundException::new);

        if (!quiz.getVideoId().equals(videoId)) {
            throw new QuizIdNotMatchException();
        }

        for (SubmittedQuizRequest submittedQuiz : submittedQuizRequestList) {
            if (courseQuizRepository.findByQuizIdAndCourseVideoId(submittedQuiz.getId(),
                    courseVideoId).isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            if (submittedQuiz.getIsCorrect() == null) {
                throw new QuizAnswerFormatNotValidException();
            }
        }
    }

    public CourseQuizEntity createCourseQuizEntity(Long courseId, Long videoId, Long courseVideoId, Long quizId,
                                                   QuizSubmittedInfo submittedInfo, Long memberId) {
        return courseQuizRepository.save(CourseQuizEntity.builder()
                .courseId(courseId)
                .quizId(quizId)
                .videoId(videoId)
                .submittedAnswer(getQuiz(quizRepository.getById(quizId))
                        .alterSubmittedAnswerFitInDB(submittedInfo.getSubmittedAnswer()))
                .correct(submittedInfo.getIsCorrect())
                .courseVideoId(courseVideoId)
                .memberId(memberId)
                .build());
    }

    public boolean isSubmittedAlready(Long courseVideoId, Long quizId) {
        QuizSubmittedInfo submittedInfo = getSubmittedInfo(quizId, courseVideoId);

        return submittedInfo.isSubmitted();
    }

    public boolean scrap(Long courseQuizId) {
        CourseQuizEntity courseQuizEntity = courseQuizRepository.getById(courseQuizId);

        courseQuizEntity.setScrapped(!courseQuizEntity.getScrapped());
        courseQuizRepository.updateById(courseQuizEntity.getId(), courseQuizEntity);

        return courseQuizEntity.getScrapped();
    }
}
