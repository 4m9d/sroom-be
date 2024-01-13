package com.m9d.sroom.material.dto.response;

import com.m9d.sroom.common.entity.jpa.QuizEntity;
import com.m9d.sroom.quiz.vo.Quiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Quiz4PdfResponse {

    private int type;

    private int index;

    private String question;

    private List<String> options;

    public Quiz4PdfResponse(Quiz quiz, int index) {
        this.type = quiz.getType().getValue();
        this.index = index;
        this.question = quiz.getQuestion();
        this.options = quiz.getOptionStrList();
    }

    public static Quiz4PdfResponse create(QuizEntity quizEntity, int index) {
        return Quiz4PdfResponse.builder()
                .type(quizEntity.getType())
                .index(index)
                .question(quizEntity.getQuestion())
                .options(quizEntity.getOptionsStr())
                .build();
    }
}
