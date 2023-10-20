package com.m9d.sroom.quiz;

import com.m9d.sroom.common.entity.CourseQuizEntity;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class QuizSubmittedInfo {

    private final boolean isSubmitted;

    private final Boolean isCorrect;

    private final String submittedAnswer;

    private final Timestamp submittedAt;

    private final Boolean isScrapped;

    public QuizSubmittedInfo(boolean isSubmitted, boolean isCorrect, String submittedAnswer, Timestamp submittedAt,
                             boolean isScrapped) {
        this.isSubmitted = isSubmitted;
        this.isCorrect = isCorrect;
        this.submittedAnswer = submittedAnswer;
        this.submittedAt = submittedAt;
        this.isScrapped = isScrapped;
    }

    public QuizSubmittedInfo(CourseQuizEntity courseQuizEntity) {
        this.isSubmitted = true;
        this.isCorrect = courseQuizEntity.getCorrect();
        this.submittedAnswer = courseQuizEntity.getSubmittedAnswer();
        this.submittedAt = courseQuizEntity.getSubmittedTime();
        this.isScrapped = courseQuizEntity.getScrapped();
    }

    public static QuizSubmittedInfo notSubmitted() {
        return new QuizSubmittedInfo(false, false, null, null,
                false);
    }
}
