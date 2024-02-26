package com.m9d.sroom.common.entity.jdbctemplate;

import com.m9d.sroom.quiz.vo.QuizOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

@Data
@Builder
@AllArgsConstructor
public class QuizOptionEntity {

    private Long quizOptionId;

    private Long quizId;

    private String optionText;

    private int optionIndex;

    public static RowMapper<QuizOptionEntity> getRowMapper() {
        return (rs, rowNum) -> QuizOptionEntity.builder()
                .quizOptionId(rs.getLong("quiz_option_id"))
                .quizId(rs.getLong("quiz_id"))
                .optionText(rs.getString("option_text"))
                .optionIndex(rs.getInt("option_index"))
                .build();

    }

    public QuizOption toQuizOption(int answer) {
        return new QuizOption(optionIndex, answer == optionIndex, optionText);
    }

    public QuizOptionEntity(Long quizId, QuizOption quizOption) {
        this.quizId = quizId;
        this.optionText = quizOption.getContent();
        this.optionIndex = quizOption.getIndex();
    }
}
