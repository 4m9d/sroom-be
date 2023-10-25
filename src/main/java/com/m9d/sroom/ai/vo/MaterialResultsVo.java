package com.m9d.sroom.ai.vo;

import com.m9d.sroom.quiz.vo.Quiz;
import com.m9d.sroom.summary.Summary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@RequiredArgsConstructor
@Getter
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

    public Summary getSummary(){
        return new Summary(summary, new Timestamp(System.currentTimeMillis()), false);
    }

    public List<Quiz> getQuizList(){
        return quizzes.stream()
                .map(QuizVo::toQuiz)
                .collect(Collectors.toList());
    }

}
