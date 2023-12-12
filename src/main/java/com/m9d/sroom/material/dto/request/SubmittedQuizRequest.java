package com.m9d.sroom.material.dto.request;

import com.m9d.sroom.quiz.vo.QuizSubmittedInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedQuizRequest {

    @Getter
    private Long id;

    private String submitted_answer;

    private Boolean is_correct;

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
