package com.m9d.sroom.quiz;

import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import com.m9d.sroom.common.entity.CourseQuizEntity;
import com.m9d.sroom.common.entity.QuizEntity;
import com.m9d.sroom.common.repository.coursequiz.CourseQuizRepository;
import com.m9d.sroom.common.repository.quiz.QuizRepository;
import com.m9d.sroom.common.repository.quizoption.QuizOptionRepository;
import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.response.QuizResponse;
import com.m9d.sroom.material.exception.CourseQuizDuplicationException;
import com.m9d.sroom.material.exception.QuizAnswerFormatNotValidException;
import com.m9d.sroom.material.exception.QuizIdNotMatchException;
import com.m9d.sroom.material.exception.QuizNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.m9d.sroom.material.constant.MaterialConstant.DEFAULT_QUIZ_OPTION_COUNT;

@Service
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseQuizRepository courseQuizRepository;
    private final QuizOptionRepository quizOptionRepository;

    public QuizService(QuizRepository quizRepository, CourseQuizRepository courseQuizRepository,
                       QuizOptionRepository quizOptionRepository) {
        this.quizRepository = quizRepository;
        this.courseQuizRepository = courseQuizRepository;
        this.quizOptionRepository = quizOptionRepository;
    }

    public List<QuizResponse> getQuizResponseList(Long videoId, Long courseVideoId) {
        List<QuizResponse> quizResponseList = new ArrayList<>();

        for (QuizEntity quizEntity : quizRepository.getListByVideoId(videoId)) {
            quizResponseList.add(new QuizResponse(quizEntity.getId(), getQuiz(quizEntity.getId()),
                    getSubmittedInfo(quizEntity.getId(), courseVideoId)));
        }
        return quizResponseList;
    }

    public Quiz getQuiz(Long quizId) {
        QuizEntity quizEntity = quizRepository.getById(quizId);

        switch (QuizType.fromValue(quizEntity.getType())) {
            case MULTIPLE_CHOICE:
                return new MultipleChoice(quizEntity.getQuestion(),
                        getQuizOptionList(quizId, quizEntity.getChoiceAnswer()), quizEntity.getChoiceAnswer());
            case SUBJECTIVE:
                return new ShortAnswerQuestion(quizEntity.getQuestion(), quizEntity.getSubjectiveAnswer());
            case TRUE_FALSE:
                return new TFQuestion(quizEntity.getQuestion(),
                        quizEntity.getChoiceAnswer(), getQuizOptionList(quizId, quizEntity.getChoiceAnswer()));
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
        QuizEntity quiz = quizRepository.findById(submittedQuizRequestList.get(0).getQuizId())
                .orElseThrow(QuizNotFoundException::new);

        if (!quiz.getVideoId().equals(videoId)) {
            throw new QuizIdNotMatchException();
        }

        for (SubmittedQuizRequest submittedQuiz : submittedQuizRequestList) {
            if (courseQuizRepository.findByQuizIdAndCourseVideoId(submittedQuiz.getQuizId(),
                    courseVideoId).isPresent()) {
                throw new CourseQuizDuplicationException();
            }

            if (submittedQuiz.getIsCorrect() == null) {
                throw new QuizAnswerFormatNotValidException();
            }
        }
    }

    public CourseQuizEntity createCourseQuizEntity(Long courseId, Long videoId, Long courseVideoId, Long quizId,
                                                   QuizSubmittedInfo submittedInfo) {
        return courseQuizRepository.save(CourseQuizEntity.builder()
                .courseId(courseId)
                .quizId(quizId)
                .videoId(videoId)
                .submittedAnswer(getQuiz(quizId).alterSubmittedAnswerFitInDB(submittedInfo.getSubmittedAnswer()))
                .correct(submittedInfo.getIsCorrect())
                .courseVideoId(courseVideoId)
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
