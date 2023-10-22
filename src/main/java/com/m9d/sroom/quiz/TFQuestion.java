package com.m9d.sroom.quiz;


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
    public int getType(){
        return QuizType.TRUE_FALSE.getValue();
    }

    @Override
    public String alterSubmittedAnswerFitInDB(String submittedAnswer) {
        return submittedAnswer.equals("true") ? "1" : "0";
    }
}
