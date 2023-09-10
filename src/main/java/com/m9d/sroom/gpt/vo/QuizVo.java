package com.m9d.sroom.gpt.vo;

import com.fasterxml.jackson.databind.JsonNode;
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
}
