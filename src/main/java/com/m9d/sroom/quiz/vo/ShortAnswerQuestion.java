package com.m9d.sroom.quiz.vo;

import com.m9d.sroom.quiz.QuizType;
import com.m9d.sroom.quiz.vo.Quiz;
import com.m9d.sroom.quiz.vo.QuizOption;

import java.util.List;

public class ShortAnswerQuestion extends Quiz {

    private final String answer;


    public ShortAnswerQuestion(String question, String answer) {
        super(question);
        this.answer = answer;
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public QuizType getType() {
        return QuizType.SUBJECTIVE;
    }

    @Override
    public List<QuizOption> getQuizOptionList() {
        return null;
    }

    @Override
    public List<String> getOptionStrList() {
        return null;
    }

    @Override
    public String alterSubmittedAnswerFitInDB(String submittedAnswer) {
        return submittedAnswer;
    }
}
