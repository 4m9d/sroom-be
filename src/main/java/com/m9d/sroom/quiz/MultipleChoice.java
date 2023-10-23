package com.m9d.sroom.quiz;

import com.m9d.sroom.material.exception.QuizAnswerFormatNotValidException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.m9d.sroom.material.constant.MaterialConstant.DEFAULT_QUIZ_OPTION_COUNT;

@Getter
public class MultipleChoice extends Quiz {

    private final List<QuizOption> optionList;

    protected final int answer;

    public MultipleChoice(String question, List<QuizOption> optionList, int answer) {
        super(question);
        this.optionList = optionList;
        this.answer = answer;
    }

    @Override
    public String getAnswer() {
        return String.valueOf(answer);
    }

    @Override
    public QuizType getType() {
        return QuizType.MULTIPLE_CHOICE;
    }

    @Override
    public List<QuizOption> getQuizOptionList() {
        return optionList;
    }

    @Override
    public List<String> getOptionStrList() {
        List<QuizOption> quizOptionList = getOptionList();

        List<String> optionStrList = new ArrayList<>(quizOptionList.size());
        for (QuizOption quizOption : quizOptionList) {
            optionStrList.add(quizOption.getIndex() - 1, quizOption.getContent());
        }
        return optionStrList;
    }

    @Override
    public String alterSubmittedAnswerFitInDB(String submittedAnswer) {
        validateSubmittedAnswer(submittedAnswer);
        return submittedAnswer;
    }

    private void validateSubmittedAnswer(String submittedAnswerReq) {
        try {
            int submittedAnswerInt = Integer.parseInt(submittedAnswerReq);
            if (submittedAnswerInt <= 0 || submittedAnswerInt > DEFAULT_QUIZ_OPTION_COUNT) {
                throw new QuizAnswerFormatNotValidException();
            }
        } catch (NumberFormatException e) {
            throw new QuizAnswerFormatNotValidException();
        }
    }
}
