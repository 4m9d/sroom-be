package com.m9d.sroom.quiz;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class Quiz {

    private final String question;

    protected Quiz(String question) {
        this.question = question;
    }

    public abstract String getAnswer();

    public abstract int getType();

    public abstract List<QuizOption> getQuizOptionList();

    public abstract List<String> getOptionStrList();

    public abstract String alterSubmittedAnswerFitInDB(String submittedAnswer);


//    public Quiz(QuizEntity quizEntity, Optional<CourseQuizEntity> courseQuizEntityOptional,
//                List<QuizOptionEntity> quizOptionEntityList) {
//        this.type = quizEntity.getType();
//        this.question = quizEntity.getQuestion();
//        this.optionList = quizOptionEntityList.stream()
//                .sorted(Comparator.comparingInt(QuizOptionEntity::getOptionIndex))
//                .map(QuizOptionEntity::getOptionText)
//                .collect(Collectors.toList());
//        switch (QuizType.fromValue(quizEntity.getType())) {
//            case MULTIPLE_CHOICE:
//                this.answer = String.valueOf(quizEntity.getChoiceAnswer());
//                break;
//            case SUBJECTIVE:
//                this.answer = quizEntity.getSubjectiveAnswer();
//                break;
//            case TRUE_FALSE:
//                this.answer = quizEntity.getChoiceAnswer().equals(0) ? "false" : "true";
//                break;
//            default:
//                throw new QuizTypeNotMatchException(quizEntity.getType());
//        }
//        this.submittedInfo = courseQuizEntityOptional.map(QuizSubmittedInfo::new)
//                .orElseGet(QuizSubmittedInfo::notSubmitted);
//    }
}
