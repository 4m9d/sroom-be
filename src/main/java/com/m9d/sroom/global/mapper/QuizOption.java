package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
public class QuizOption {

    private Long quizOptionId;

    private Long quizId;

    private String optionText;

    private int optionIndex;

    public static RowMapper<QuizOption> getRowMapper() {
        return (rs, rowNum) -> QuizOption.builder()
                .quizOptionId(rs.getLong("quiz_option_id"))
                .quizId(rs.getLong("quiz_id"))
                .optionText(rs.getString("option_text"))
                .optionIndex(rs.getInt("option_index"))
                .build();

    }
}
