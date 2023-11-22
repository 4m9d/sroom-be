package com.m9d.sroom.common.repository.materialfeedback;

import com.m9d.sroom.common.entity.MaterialFeedbackEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MaterialFeedbackJdbcRepositoryImpl implements MaterialFeedbackRepository {

    private final JdbcTemplate jdbcTemplate;

    public MaterialFeedbackJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MaterialFeedbackEntity save(MaterialFeedbackEntity feedbackEntity) {
        jdbcTemplate.update(MaterialFeedbackRepositorySql.SAVE,
                feedbackEntity.getMemberId(),
                feedbackEntity.getContentId(),
                feedbackEntity.getContentType().getValue(),
                feedbackEntity.isSatisfactory());
        return getById(jdbcTemplate.queryForObject(MaterialFeedbackRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public MaterialFeedbackEntity getById(Long id) {
        return jdbcTemplate.queryForObject(MaterialFeedbackRepositorySql.GET_BY_ID,
                MaterialFeedbackEntity.getRowMapper(), id);
    }

    @Override
    public Optional<MaterialFeedbackEntity> findByMemberIdAndTypeAndMaterialId(Long memberId, int materialType,
                                                                               Long materialId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    MaterialFeedbackRepositorySql.GET_BY_MEMBER_ID_AND_TYPE_AND_MATERIAL_ID,
                    MaterialFeedbackEntity.getRowMapper(), memberId, materialType, materialId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
