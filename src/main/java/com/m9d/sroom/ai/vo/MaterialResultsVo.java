package com.m9d.sroom.ai.vo;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@RequiredArgsConstructor
public class MaterialResultsVo {

    private String video_id;

    private int is_valid;

    private String summary;

    private List<QuizVo> quizzes;

    public String getVideoId() {
        return video_id;
    }

    public int getIsValid() {
        return is_valid;
    }

    public String getSummary() {
        return summary;
    }

    public List<QuizVo> getQuizzes() {
        return quizzes;
    }
}
