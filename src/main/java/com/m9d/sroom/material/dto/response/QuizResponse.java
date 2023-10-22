package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.quiz.Quiz;
import com.m9d.sroom.quiz.QuizSubmittedInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuizResponse {

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

    public QuizResponse (Long quizId, Quiz quiz, QuizSubmittedInfo submittedInfo) {
        this.id = quizId;
        this.type = quiz.getType();
        this.question = quiz.getQuestion();
        this.options = quiz.getOptionStrList();
        this.submitted = submittedInfo.isSubmitted();
        this.answer = quiz.getAnswer();
        if(submitted){
            this.submittedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(submittedInfo.getSubmittedAt());
            this.submittedAnswer = submittedInfo.getSubmittedAnswer();
        }else{
            this.submittedAt = null;
            this.submittedAnswer = null;
        }
        this.scrapped = submittedInfo.getIsScrapped();
    }
}
