package com.m9d.sroom.material.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.quiz.vo.Quiz;
import com.m9d.sroom.quiz.vo.QuizSubmittedInfo;
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

    private FeedbackInfo feedbackInfo;

    public QuizResponse (Long quizId, Quiz quiz, QuizSubmittedInfo submittedInfo, FeedbackInfo feedbackInfo) {
        this.id = quizId;
        this.type = quiz.getType().getValue();
        this.question = quiz.getQuestion();
        this.options = quiz.getOptionStrList();
        this.submitted = submittedInfo.isSubmitted();
        this.answer = quiz.getAnswer();
        if(submitted){
            this.submittedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(submittedInfo.getSubmittedAt());
            this.submittedAnswer = submittedInfo.getSubmittedAnswer();
            this.correct = submittedAnswer.equals(quiz.getAnswer());
        }else{
            this.submittedAt = null;
            this.submittedAnswer = null;
            this.correct = false;
        }
        this.scrapped = submittedInfo.getIsScrapped();
        this.feedbackInfo = feedbackInfo;
    }
}
