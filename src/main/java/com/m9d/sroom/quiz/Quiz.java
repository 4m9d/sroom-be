package com.m9d.sroom.quiz;

import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Quiz {

    private final String question;

    protected Quiz(String question) {
        this.question = question;
    }

    public abstract String getAnswer();

    public abstract QuizType getType();

    public abstract List<QuizOption> getQuizOptionList();

    public abstract List<String> getOptionStrList();

    public abstract String alterSubmittedAnswerFitInDB(String submittedAnswer);

    public static Quiz toInheritor(QuizType type, String question, List<String> optionStrList, String answer) {
        if (type.equals(QuizType.MULTIPLE_CHOICE)) {
            return new MultipleChoice(question, toQuizOption(Integer.parseInt(answer), optionStrList),
                    Integer.parseInt(answer));
        } else if (type.equals(QuizType.SUBJECTIVE)) {
            return new ShortAnswerQuestion(question, answer);
        } else if (type.equals(QuizType.TRUE_FALSE)) {
            return new TFQuestion(question, Integer.parseInt(answer),
                    toQuizOption(Integer.parseInt(answer), optionStrList));
        } else {
            return null;
        }
    }

    private static List<QuizOption> toQuizOption(int answer, List<String> optionStrList) {
        List<QuizOption> optionList = new ArrayList<>();
        for (int i = 1; i < optionStrList.size() + 1; i++) {
            optionList.add(new QuizOption(i, i == answer, optionStrList.get(i)));
        }
        return optionList;
    }
}
