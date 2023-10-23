package com.m9d.sroom.ai.vo;

import com.m9d.sroom.quiz.Quiz;
import com.m9d.sroom.quiz.QuizType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@RequiredArgsConstructor
public class QuizVo {

    private int quiz_type;

    private String quiz_question;

    private List<String> quiz_select_options;

    @Getter
    private String answer;

    public int getQuizType() {
        return quiz_type;
    }

    public String getQuizQuestion() {
        return quiz_question;
    }

    public List<String> getOptions() {
        return quiz_select_options;
    }

    public Quiz toQuiz() {
        return Quiz.toInheritor(QuizType.fromValue(quiz_type), quiz_question, quiz_select_options, answer);
    }
}
