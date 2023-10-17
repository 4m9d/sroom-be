package com.m9d.sroom.common.entity;

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

    public static RowMapper<QuizEntity> getRowMapper() {
        return (rs, rowNum) -> QuizEntity.builder()
                .id(rs.getLong("quiz_id"))
                .videoId(rs.getLong("video_id"))
                .type(rs.getInt("type"))
                .question(rs.getString("question"))
                .subjectiveAnswer(rs.getString("subjective_answer"))
                .choiceAnswer(rs.getInt("choice_answer"))
                .build();
    }
}
