package com.m9d.sroom.common.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class Quiz {

    private Long id;

    private Long videoId;

    private int type;

    private String question;

    private String subjectiveAnswer;

    private Integer choiceAnswer;

    public static RowMapper<Quiz> getRowMapper() {
        return (rs, rowNum) -> Quiz.builder()
                .id(rs.getLong("quiz_id"))
                .videoId(rs.getLong("video_id"))
                .type(rs.getInt("type"))
                .question(rs.getString("question"))
                .subjectiveAnswer(rs.getString("subjective_answer"))
                .choiceAnswer(rs.getInt("choice_answer"))
                .build();
    }
}
