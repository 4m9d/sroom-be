package com.m9d.sroom.common.repository.recommend;

import com.m9d.sroom.common.entity.jdbctemplate.RecommendEntity;

import java.util.List;

public interface RecommendRepository {
    List<RecommendEntity> getListByDomain(int domainId);
}
