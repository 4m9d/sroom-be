package com.m9d.sroom.quiz.vo;


import com.m9d.sroom.quiz.QuizType;

import java.util.List;

public class TFQuestion extends MultipleChoice {

    public TFQuestion(String question, int answer, List<QuizOption> optionList) {
        super(question, optionList, answer);
    }

    @Override
    public String getAnswer() {
        return answer == 1 ? "true" : "false";
    }

    @Override
    public QuizType getType(){
        return QuizType.TRUE_FALSE;
    }

    @Override
    public String alterSubmittedAnswerFitInDB(String submittedAnswer) {
        return submittedAnswer.equals("true") ? "1" : "0";
    }
}
