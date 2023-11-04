package com.m9d.sroom.material.dto.response;

import com.m9d.sroom.quiz.vo.Quiz;
import lombok.Data;

import java.util.List;

@Data
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
}
