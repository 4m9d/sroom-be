package com.m9d.sroom.material.dto.request;

import lombok.Setter;

@Setter
public class SubmittedQuiz {

    private Long id;

    private String submitted_answer;

    private Boolean is_correct;

    public Long getQuizId() {
        return id;
    }

    public String getSubmittedAnswer() {
        return submitted_answer;
    }

    public Boolean getIsCorrect() {
        return is_correct;
    }
}
