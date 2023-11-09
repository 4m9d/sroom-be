package com.m9d.sroom.common.repository.recommend;

import com.m9d.sroom.common.entity.RecommendEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecommendJdbcRepositoryImpl implements RecommendRepository{

    private final JdbcTemplate jdbcTemplate;

    public RecommendJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<RecommendEntity> getListByDomain(int domainId) {
        return jdbcTemplate.query(RecommendRepositorySql.GET_LIST_BY_DOMAIN, RecommendEntity.getMapper(), domainId);
    }
}
