package com.m9d.sroom.material.dto.request;

import lombok.Setter;

@Setter
public class SubmittedQuiz {

    private Long quiz_id;

    private Integer submitted_answer;

    private Boolean is_correct;

    public Long getQuizId() {
        return quiz_id;
    }

    public Integer getSubmittedAnswer() {
        return submitted_answer;
    }

    public Boolean getIsCorrect() {
        return is_correct;
    }
}
