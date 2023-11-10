package com.m9d.sroom.common.repository.materialfeedback;

import com.m9d.sroom.common.entity.MaterialFeedbackEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public class MaterialFeedbackJdbcRepositoryImpl implements MaterialFeedbackRepository{

    private final JdbcTemplate jdbcTemplate;

    public MaterialFeedbackJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MaterialFeedbackEntity save(MaterialFeedbackEntity feedbackEntity) {
        jdbcTemplate.update(MaterialFeedbackRepositorySql.SAVE,
                feedbackEntity.getMemberId(),
                feedbackEntity.getContentId(),
                feedbackEntity.getContentType(),
                feedbackEntity.getRating());
        return getById(jdbcTemplate.queryForObject(MaterialFeedbackRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public MaterialFeedbackEntity getById(Long id) {
        return jdbcTemplate.queryForObject(MaterialFeedbackRepositorySql.GET_BY_ID,
                MaterialFeedbackEntity.getRowMapper(), id);
    }

    @Override
    public Boolean checkQuizFeedbackExist(Long memberId, Long quizId) {
        return jdbcTemplate.queryForObject(MaterialFeedbackRepositorySql.CHECK_BY_MEMBER_ID_AND_QUIZ_ID,
                Boolean.class, memberId, quizId);
    }

    @Override
    public Boolean checkSummaryFeedbackExist(Long memberId, Long summaryId) {
        return jdbcTemplate.queryForObject(MaterialFeedbackRepositorySql.CHECK_BY_MEMBER_ID_AND_SUMMARY_ID,
                Boolean.class, memberId, summaryId);
    }
}
