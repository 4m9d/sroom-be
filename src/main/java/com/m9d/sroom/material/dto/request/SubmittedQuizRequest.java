package com.m9d.sroom.material.dto.request;

import com.m9d.sroom.quiz.QuizSubmittedInfo;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
public class SubmittedQuizRequest {

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

    public QuizSubmittedInfo toVo() {
        return new QuizSubmittedInfo(true, this.getIsCorrect(), this.getSubmittedAnswer(),
                new Timestamp(System.currentTimeMillis()), false);
    }
}
