package com.m9d.sroom.quiz;

import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import com.m9d.sroom.common.entity.CourseQuizEntity;
import com.m9d.sroom.common.entity.QuizEntity;
import com.m9d.sroom.common.entity.QuizOptionEntity;
import com.m9d.sroom.material.model.QuizType;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Quiz {

    private final int type;

    private final String question;

    private final List<String> optionList;

    private final String answer;

    private final QuizSubmittedInfo submittedInfo;

    public Quiz(int type, String question, List<String> optionList, String answer, QuizSubmittedInfo submittedInfo) {
        this.type = type;
        this.question = question;
        this.optionList = optionList;
        this.answer = answer;
        this.submittedInfo = submittedInfo;
    }

    public Quiz(QuizEntity quizEntity, Optional<CourseQuizEntity> courseQuizEntityOptional,
                List<QuizOptionEntity> quizOptionEntityList) {
        this.type = quizEntity.getType();
        this.question = quizEntity.getQuestion();
        this.optionList = quizOptionEntityList.stream()
                .sorted(Comparator.comparingInt(QuizOptionEntity::getOptionIndex))
                .map(QuizOptionEntity::getOptionText)
                .collect(Collectors.toList());
        switch (QuizType.fromValue(quizEntity.getType())) {
            case MULTIPLE_CHOICE:
                this.answer = String.valueOf(quizEntity.getChoiceAnswer());
                break;
            case SUBJECTIVE:
                this.answer = quizEntity.getSubjectiveAnswer();
                break;
            case TRUE_FALSE:
                this.answer = quizEntity.getChoiceAnswer().equals(0) ? "false" : "true";
                break;
            default:
                throw new QuizTypeNotMatchException(quizEntity.getType());
        }
        this.submittedInfo = courseQuizEntityOptional.map(QuizSubmittedInfo::new)
                .orElseGet(QuizSubmittedInfo::notSubmitted);
    }
}
