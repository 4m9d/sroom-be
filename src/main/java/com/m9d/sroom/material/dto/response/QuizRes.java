package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.material.model.QuizType;
import com.m9d.sroom.quiz.Quiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuizRes {

    private Long id;

    private int type;

    private String question;

    private List<String> options;

    @JsonProperty("is_submitted")
    private Boolean submitted;

    private String answer;

    private String submittedAt;

    private String submittedAnswer;

    @JsonProperty("is_correct")
    private boolean correct;

    @JsonProperty("is_scrapped")
    private boolean scrapped;

    public QuizRes(long quizId, Quiz quiz){
        this.id = quizId;
        this.type = quiz.getType();
        this.question = quiz.getQuestion();
        this.options = quiz.getOptionList();
        this.submitted = quiz.getSubmittedInfo().isSubmitted();
        this.answer = quiz.getAnswer();
        if(submitted){
            this.submittedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(quiz.getSubmittedInfo()
                    .getSubmittedAt());
            if (quiz.getType() == QuizType.TRUE_FALSE.getValue()) {
                this.submittedAnswer = quiz.getSubmittedInfo().getSubmittedAnswer().equals("0") ? "false" : "true";
            } else {
                this.submittedAnswer = quiz.getSubmittedInfo().getSubmittedAnswer();
            }
            this.correct = quiz.getSubmittedInfo().getIsCorrect();
            this.scrapped = quiz.getSubmittedInfo().getIsScrapped();
        }
    }
}
