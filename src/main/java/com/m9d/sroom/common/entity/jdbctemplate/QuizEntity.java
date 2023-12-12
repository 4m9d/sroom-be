package com.m9d.sroom.common.entity.jdbctemplate;

import com.m9d.sroom.quiz.vo.Quiz;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class QuizEntity {

    private Long id;

    private Long videoId;

    private int type;

    private String question;

    private String subjectiveAnswer;

    private Integer choiceAnswer;

    private Integer positiveFeedbackCount;

    private Integer negativeFeedbackCount;

    public static RowMapper<QuizEntity> getRowMapper() {
        return (rs, rowNum) -> QuizEntity.builder()
                .id(rs.getLong("quiz_id"))
                .videoId(rs.getLong("video_id"))
                .type(rs.getInt("type"))
                .question(rs.getString("question"))
                .subjectiveAnswer(rs.getString("subjective_answer"))
                .choiceAnswer(rs.getInt("choice_answer"))
                .positiveFeedbackCount(rs.getInt("positive_feedback_count"))
                .negativeFeedbackCount(rs.getInt("negative_feedback_count"))
                .build();
    }

    public static QuizEntity createMultipleChoiceQuizEntity(Long videoId, Quiz quiz){
        return QuizEntity.builder()
                .videoId(videoId)
                .type(quiz.getType().getValue())
                .question(quiz.getQuestion())
                .choiceAnswer(Integer.parseInt(quiz.getAnswer()))
                .build();

    }

    public static QuizEntity createShortAnswerQuizEntity(Long videoId, Quiz quiz){
        return QuizEntity.builder()
                .videoId(videoId)
                .type(quiz.getType().getValue())
                .question(quiz.getQuestion())
                .subjectiveAnswer(quiz.getAnswer())
                .build();
    }
}
